package main.command;

import command.CommandResult;
import command.GenericCommand;
import communication.ServerException;
import entity.GenericEntity;
import entity.GenericEntityMap;
import main.communication.RequestType;
import main.communication.request.CommandRequest;
import util.ByteManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class that handles the running of a generic command
 */
public class GenericCommandHandler {

    private GenericCommandFactory commandFactory;

    public GenericCommandHandler() {
        this.commandFactory = new GenericCommandFactory();
    }

    public void setCommandFactory(GenericCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Takes a command request and using the factory to retrieve the correct command and run it
     *
     * @param request the request holding the data for the command to be run
     *
     * @return the result of running the command
     */
    public CommandResult handleCommand(CommandRequest request) {
        GenericCommand command;
        try {
            command = commandFactory.getCommand(request);
        } catch(ServerException e) {
            return new CommandResult(e.getMessage(), false, request.getEntityId());
        }
        command.setParameters(request.getParameters());
        // Get the result of the command so the client knows what the value is from
        CommandResult result = command.run();
        // Set the entityId and command name in the result
        result.setEntityId(request.getEntityId());
        result.setCommand(request.getCommand());
        result.setUdata(request.getUdata());
        // Return the result
        return result;
    }

    public byte[] handleCommand(byte[] requestBytes) {
        // Parse the entity type and placeholder ID from the request bytes
        int entityId = ByteBuffer.wrap(requestBytes, 0, 4).getInt();
        int commandId = ByteBuffer.wrap(requestBytes, 4, 4).getInt();

        // The next byte tells us if we have a parameter or not
        int hasParameter = ByteBuffer.wrap(requestBytes, 8, 1).get();
        Object[] commandParameter = new Object[0];

        // If we have a parameter, parse it out, set out parameters with it
        if (hasParameter == 1) {
            commandParameter = new Object[1];
            commandParameter[0] = Arrays.copyOfRange(requestBytes, 9, requestBytes.length);
        }

        // Track success and error message
        boolean success = true;
        String errorMessage = "";
        byte[] commandReturnValue = new byte[]{};

        GenericCommand command = null;
        try {
            command = getCommand(entityId, commandId);
        }
        catch(ServerException e) {
            // If there was an exception, we failed getting the command
            success = false;
            errorMessage = "Error running command for entity: " + entityId + "\nError: " + e.getMessage();
        }

        CommandResult runResult = null;

        // If the command was initialized
        if (command != null) {
            // Set the command parameters
            command.setParameters(commandParameter);

            // Get the result of the command so the client knows what the value is from
            CommandResult result = command.run();

            // Unpack the result into our values
            success = result.getSuccess();
            errorMessage = result.getErrorMessage();

            // Unpack the value returned and cast it to a byte array if possible
            Object obj = result.getValue();
            if (obj != null
                    && obj.getClass().isArray()
                    && obj.getClass().getComponentType() == byte.class) {
                commandReturnValue = (byte[]) obj;
            }

            // If the object wasn't a byte array, throw an error, we only accept byte arrays
            else {
                success = false;
                errorMessage = "Error: Return type was not bytes.";
            }
        }

        // Return the result
        return compileCommandResult(success, entityId, commandId, commandReturnValue, errorMessage);
    }

    public GenericCommand getCommand(int entityId, int commandId) throws ServerException {
        // Get the map of entities
        GenericEntityMap entities = GenericEntityMap.getInstance();

        // Get the entity that has this entity id
        GenericEntity entity = entities.get(Integer.toString(entityId));

        // If the entity is null, we could not find the entity with that id
        if(entity == null) {
            throw new ServerException("Invalid entity ID");
        }

        // Return the commandId on the entity
        return entity.getCommand(commandId);
    }

    /**
     * Compiles the result into a byte array to send back to the server.
     * If success is true, then the message is the type (int), an int with the number of bytes, a 1, signifying success, along
     * with two ints signifying the entityId and the placeholderId
     * Otherwise, the message is the type, an int with the number of bytes, a 0 (byte), and the bytes representing the
     * error message.
     * @param success Whether or not we were able to successfully setup the entity data we recieved
     * @param entityId The entity id
     * @param errorMessage Empty is success is true
     * @return The array of bytes to send back to the client
     */
    private byte[] compileCommandResult(boolean success, int entityId, int commandId, byte[] commandReturn, String errorMessage) {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.COMMAND.getNumVal());
        result.add((byte)0);

        // If the setup was a success, then all we need is:
        // 1. length of message
        // 2. 2 bytes set to 1 for success
        // 3. 2 ints (8 bytes) for entity id
        // 4. Add commandReturn bytes to array
        if (success) {
            // Add the message length to our byte array
            // 2 for the success, 8 for the entityId and commandId, and the commandreturn length
            int messageLength = 2 + 8 + commandReturn.length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);

            // Add the entityId to the array
            ByteManager.addIntToByteArray(entityId, result);

            // Add the commandId to the array
            ByteManager.addIntToByteArray(commandId, result);

            // Add the command return to the array
            ByteManager.addBytesToArray(commandReturn, result);
        }
        // Otherwise, send an error message
        else {
            // Add the integer 2 + length of error message to our byte array, this is the rest of the bytes
            int messageLength = 2 + errorMessage.getBytes().length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Failure
            result.add((byte)0);
            result.add((byte)0);

            // Message
            ByteManager.addBytesToArray(errorMessage.getBytes(), result);
        }

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        return resultArray;
    }
}
