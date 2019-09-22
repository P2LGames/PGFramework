package main.util;

import entity.GenericEntity;
import main.communication.RequestType;
import entity.GenericEntityMap;
import util.ByteManager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class InMemoryClassLoader {

    /**
     * Attempts to update a GenericEntity's command data with the passed request bytes.
     * @param requestBytes Array of bytes with all necessary information to get the entity and update it's command info.
     * @return Byte array as a reponse, whether or not we were successful
     */
    public byte[] updateClass(GenericEntityMap entityMap, byte[] requestBytes) {
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
        String className = new String(Arrays.copyOfRange(requestBytes, starting, starting + classNameLength), StandardCharsets.US_ASCII);
        starting += classNameLength;

        // FILE CONTENTS length in bytes
        int fileContentsLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // FILE CONTENTS as string from bytes
        String fileContents = new String(Arrays.copyOfRange(requestBytes, starting, starting + fileContentsLength), StandardCharsets.US_ASCII);

        // Track success and error message
        boolean success = true;
        String errorMessage = "";

        try {
            // Create the compiler
            InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

            // Load the class and compile it
            Class<?> loadedClass = compiler.compile(className, fileContents);

            // Get the entity via the entity id
            GenericEntity entity = entityMap.get(Integer.toString(entityId));

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

        return this.compileFileUpdateResult(success, entityId, commandId, className, errorMessage);
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
    private byte[] compileFileUpdateResult(boolean success, int entityId, int commandId, String className, String errorMessage) {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.FILE_UPDATE.getNumVal());
        result.add((byte)0);

        // Convert the className to a byte array
        byte[] classNameBytes = className.getBytes();

        // If the setup was a success, then all we need is:
        // 1. length of message
        // 2. 2 bytes set to 1 for success
        // 3. 2 ints (8 bytes) for entity id and command id
        if (success) {
            // Add the message length to our byte array
            // 2 for the success, 8 for the entityId and commandId, and 4 (int) + the length of the className
            int messageLength = 2 + 8 + 4 + classNameBytes.length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);

            // Add the entityId to the array
            ByteManager.addIntToByteArray(entityId, result);

            // Add the commandId to the array
            ByteManager.addIntToByteArray(commandId, result);

            // Add the length of the class name bytes
            ByteManager.addIntToByteArray(classNameBytes.length, result);

            // Add the class name bytes
            ByteManager.addBytesToArray(classNameBytes, result);
        }
        // Otherwise, send an error message
        else {
            // Add the integer 2 + length of error message to our byte array, this is the rest of the bytes
            int messageLength = 10 + 4 + classNameBytes.length + errorMessage.getBytes().length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Failure
            result.add((byte)0);
            result.add((byte)0);

            // Add the entity id and command id
            ByteManager.addIntToByteArray(entityId, result);
            ByteManager.addIntToByteArray(commandId, result);

            // Add the length of the class name bytes
            ByteManager.addIntToByteArray(classNameBytes.length, result);

            // Add the class name bytes
            ByteManager.addBytesToArray(classNameBytes, result);

            // Message
            ByteManager.addBytesToArray(errorMessage.getBytes(), result);
        }

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        return resultArray;
    }

}
