package main.util;

import entity.GenericEntity;
import main.command.CommandError;
import main.command.MonitorableThread;
import main.communication.ClientHandler;
import main.communication.RequestType;
import entity.GenericEntityMap;
import util.ByteManager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class InMemoryClassLoader extends Thread implements MonitorableThread {

    private ClientHandler handler;
    private GenericEntityMap entityMap;
    private byte[] requestBytes;

    private boolean finished = false;
    private boolean timedOut = false;

    public int entityId;
    public int commandId;
    String filePath;
    String className;
    String fileContents;

    public InMemoryClassLoader(ClientHandler handler, GenericEntityMap entityMap, byte[] requestBytes) {
        this.handler = handler;
        this.entityMap = entityMap;
        this.requestBytes = requestBytes;
    }

    @Override
    public void run() {
        // Track where we are in the request bytes
        int starting = 0;

        // Unpack our bytes, lots of data
        // ENTITY ID
        entityId = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // COMMAND ID in bytes
        commandId = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;

        // FILE PATH from bytes
        int filePathLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;
        filePath = new String(Arrays.copyOfRange(requestBytes, starting, starting + filePathLength), StandardCharsets.US_ASCII);
        starting += filePathLength;

        // CLASS NAME from bytes
        int classNameLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;
        className = new String(Arrays.copyOfRange(requestBytes, starting, starting + classNameLength), StandardCharsets.US_ASCII);
        starting += classNameLength;

        // FILE CONTENTS from bytes
        int fileContentsLength = ByteBuffer.wrap(requestBytes, starting, 4).getInt();
        starting += 4;
        fileContents = new String(Arrays.copyOfRange(requestBytes, starting, starting + fileContentsLength), StandardCharsets.US_ASCII);

        // Get the result from the command
        byte[] result = updateClass();

        // If we did not time out
        if (!timedOut) {
            // Send the data back to the client handler
            handler.sendByteArray(result);
        }

        // Mark ourselves as finished
        finished = true;
    }

    /**
     * Attempts to update a GenericEntity's command data with the passed request bytes.
     * @return Byte array as a reponse, whether or not we were successful
     */
    public byte[] updateClass() {

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
            errorMessage = e.getLocalizedMessage();
        }

        return this.compileFileUpdateResult(success, errorMessage);
    }

    /**
     * Compiles the result into a byte array to send back to the server.
     * If success is true, then the message is the type (int), an int with the number of bytes, a 1, signifying success, along
     * with two ints signifying the entityId and the commandId
     * Otherwise, the message is the type, an int with the number of bytes, a 0 (byte), and the bytes representing the
     * error message.ds
     * @param success Whether or not we were able to successfully setup the entity data we recieved
     * @param errorMessage Empty if success is true
     * @return The array of bytes to send back to the client
     */
    private byte[] compileFileUpdateResult(boolean success, String errorMessage) {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.FILE_UPDATE.getNumVal());
        result.add((byte)0);

        // Convert the className to a byte array
        byte[] filePathBytes = filePath.getBytes();
        byte[] classNameBytes = className.getBytes();

        // If the setup was a success, then all we need is:
        // 1. length of message
        // 2. 2 bytes set to 1 for success
        // 3. 2 ints (8 bytes) for entity id and command id
        if (success) {
            // Add the message length to our byte array
            // 2 for the success, 8 for the entityId and commandId, and 4 * 2 (int) + the length of the className & filePath
            int messageLength = 18 + filePathBytes.length + classNameBytes.length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);

            // Add the entity and command ID to the array
            ByteManager.addIntToByteArray(entityId, result);
            ByteManager.addIntToByteArray(commandId, result);

            // Add the file path to the bytes
            ByteManager.addIntToByteArray(filePathBytes.length, result);
            ByteManager.addBytesToArray(filePathBytes, result);

            // Add the class name to the bytes
            ByteManager.addIntToByteArray(classNameBytes.length, result);
            ByteManager.addBytesToArray(classNameBytes, result);
        }
        // Otherwise, send an error message
        else {
            // Add the integer 2 + 8 for the entityId and commandId, and 4 * 2 (int) + the length of the className & filePath, then the errorMessage
            int messageLength = 18 + filePathBytes.length + classNameBytes.length + errorMessage.getBytes().length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Failure
            result.add((byte)0);
            result.add((byte)0);

            // Add the entity id and command id
            ByteManager.addIntToByteArray(entityId, result);
            ByteManager.addIntToByteArray(commandId, result);

            // Add the file path to the bytes
            ByteManager.addIntToByteArray(filePathBytes.length, result);
            ByteManager.addBytesToArray(filePathBytes, result);

            // Add the class name to the bytes
            ByteManager.addIntToByteArray(classNameBytes.length, result);
            ByteManager.addBytesToArray(classNameBytes, result);

            // Message
            ByteManager.addBytesToArray(errorMessage.getBytes(), result);
        }

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        return resultArray;
    }

    public byte[] compileTimeoutError() {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.COMMAND_ERROR.getNumVal());
        result.add((byte)0);

        String errorMessage = "Command took too long to finish, possible infinite loop.";

        // Add the integer 10 + length of error message to our byte array, this is the rest of the bytes
        int messageLength = 10 + errorMessage.getBytes().length;
        ByteManager.addIntToByteArray(messageLength, result);

        // Command error type
        result.add((byte) CommandError.TIMEOUT.getNumVal());
        result.add((byte)0);

        // Add the entity id and command id
        ByteManager.addIntToByteArray(entityId, result);
        ByteManager.addIntToByteArray(commandId, result);

        // Message
        ByteManager.addBytesToArray(errorMessage.getBytes(), result);

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        // Return the resulting array of bytes
        return resultArray;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setTimedOut(boolean timedOut) { this.timedOut = timedOut; }

    public int getEntityId() { return entityId; }

}
