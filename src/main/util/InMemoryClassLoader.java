package main.util;

import annotations.SetEntity;
import command.GenericCommand;
import entity.GenericEntity;
import main.communication.RequestType;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import entity.GenericEntityMap;
import util.ByteManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class InMemoryClassLoader {

    /**
     * Attempts to compile and load a class and returns the result
     *
     * @param request
     *   The request object containing the class name and source code
     * @return
     *   The result object the reports a failure or contains the loaded class
     */
    public UpdateResult updateClass(UpdateRequest request) {
        UpdateResult result = new UpdateResult(request.getEntityId());
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

        try {
            Class<?> loadedClass = compiler.compile(request.getClassName(), request.getFileContents());
            this.saveSourceCode(request, loadedClass);
            GenericCommand command = new GenericCommand();
            String[] paramTypesStrings = request.getParameterTypesStrings();
            Class<?>[] paramTypes = new Class<?>[paramTypesStrings.length];

            for(int i = 0; i < paramTypesStrings.length; i++) {
                paramTypes[i] = Class.forName(paramTypesStrings[i]);
            }

            // Get the entity via the entity id
            GenericEntity entity = GenericEntityMap.getInstance().get(request.getEntityId());

            // Create a class object with the loaded class
            Object classObject = loadedClass.getConstructor().newInstance();

            // Loop through the methods in the loaded class
            for (Method commandMethod : loadedClass.getDeclaredMethods()) {
                // If we find a SetEntity method, we want to invoke it with the entity found via entityId
                if (commandMethod.isAnnotationPresent(SetEntity.class)) {
                    // Set the entity of our class object
                    commandMethod.invoke(classObject, entity);
                    // Exit the loop
                    break;
                }
            }

            // Set the method and class object for this generic command
            command.setMethod(loadedClass.getMethod(request.getMethodName(), paramTypes));
            command.setClassObject(classObject);

            // Update the command for the entity
            entity.updateCommand(request.getCommand(), command);
            entity.addCommandClass(request.getClassName());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Error: " + e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * Saves the source code for a compiled class in a .java file for reference
     * @param request
     *    The request object containing the class name and source code
     * @param loadedClass
     *    The compiled class object
     */
    private void saveSourceCode(UpdateRequest request, Class<?> loadedClass) {
        String packageName = loadedClass.getPackageName();

        // Get the package
        if (packageName.equals("")) {
            packageName = "NoPackage";
        }

        // Replace periods with slashes
        packageName = packageName.replace('.', File.separatorChar);

        // Get the path to the package folder
        String path = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator + packageName;

        // Make the directories if they do not exist
        new File(path).mkdirs();

        // Get the file path
        String fileName = path + File.separator + loadedClass.getSimpleName() + ".java";

        System.out.println(request.getFileContents());

        try {
            // Write the file with the file contents
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write(request.getFileContents());
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Attempts to update a GenericEntity's command data with the passed request bytes.
     * @param requestBytes Array of bytes with all necessary information to get the entity and update it's command info.
     * @return Byte array as a reponse, whether or not we were successful
     */
    public byte[] updateClass(byte[] requestBytes) {
        // Track where we are in the request bytes
        int starting = 0;

        // Unpack our bytes, lots of data
        // ENTITY ID
        int entityId = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // COMMAND ID in bytes
        int commandId = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // CLASS NAME length in bytes
        int classNameLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // CLASS NAME as string from bytes
        String className = new String(Arrays.copyOfRange(requestBytes, starting, starting + classNameLength));
        starting += classNameLength;

        // FILE CONTENTS length in bytes
        int fileContentsLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // FILE CONTENTS as string from bytes
        String fileContents = new String(Arrays.copyOfRange(requestBytes, starting, starting + fileContentsLength));

        // Track success and error message
        boolean success = true;
        String errorMessage = "";

        try {
            // Create the compiler
            InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

            // Load the class and compile it
            Class<?> loadedClass = compiler.compile(className, fileContents);

            // Get the entity via the entity id
            GenericEntity entity = GenericEntityMap.getInstance().get(Integer.toString(entityId));

            // Depending on the command id, update the entity with the loaded class
            // If the command id is -1, then update all commands with the class
            if (commandId == -1) {
                entity.setupCommandsWithClass(loadedClass);
            }
            // Otherwise, update the specific command with the given class
            else {
                entity.setupCommandIdWithClass(loadedClass, commandId);
            }
        }
        catch (Exception e) {
            success = false;
            errorMessage = "Error: " + e.getLocalizedMessage();
        }

        return this.compileFileUpdateResult(success, entityId, commandId, errorMessage);
    }

    /**
     * Compiles the result into a byte array to send back to the server.
     * If success is true, then the message is the type (int), an int with the number of bytes, a 1, signifying success, along
     * with two ints signifying the entityId and the commandId
     * Otherwise, the message is the type, an int with the number of bytes, a 0 (byte), and the bytes representing the
     * error message.
     * @param success Whether or not we were able to successfully setup the entity data we recieved
     * @param entityId The entity id
     * @param commandId The id of the command
     * @param errorMessage Empty is success is true
     * @return The array of bytes to send back to the client
     */
    private byte[] compileFileUpdateResult(boolean success, int entityId, int commandId, String errorMessage) {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.FILE_UPDATE.getNumVal());
        result.add((byte)0);

        // If the setup was a success, then all we need is:
        // 1. length of message
        // 2. 2 bytes set to 1 for success
        // 3. 2 ints (8 bytes) for entity id and command id
        if (success) {
            // Add the message length to our byte array
            // 2 for the success, 8 for the entityId and commandId
            int messageLength = 2 + 8;
            ByteManager.addIntToByteArray(messageLength, result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);

            // Add the entityId to the array
            ByteManager.addIntToByteArray(entityId, result);

            // Add the commandId to the array
            ByteManager.addIntToByteArray(commandId, result);
        }
        // Otherwise, send an error message
        else {
            // Add the integer 2 + length of error message to our byte array, this is the rest of the bytes
            int messageLength = 10 + errorMessage.getBytes().length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Failure
            result.add((byte)0);
            result.add((byte)0);

            // Add the entity id and command id
            ByteManager.addIntToByteArray(entityId, result);
            ByteManager.addIntToByteArray(commandId, result);

            // Message
            ByteManager.addBytesToArray(errorMessage.getBytes(), result);
        }

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        return resultArray;
    }

}
