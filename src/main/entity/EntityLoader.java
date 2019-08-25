package main.entity;

import entity.GenericEntity;
import entity.GenericEntityMap;
import main.communication.ClientHandler;
import main.communication.RequestType;
import util.ByteManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Loads the entities into the server
 */
public class EntityLoader {

    /**
     * Creates an entity of the specified type, assigns it an id, registers it, and returns the id
     *
     * @param requestBytes
     *  The bytes to be unpacked to register the entity
     * @return
     *  The bytes that represent whether or not the entity was successfully registered
     */
    public synchronized byte[] registerEntity(ClientHandler handler, byte[] requestBytes) {
        // Parse the entity type and placeholder ID from the request bytes
        Integer entityType = ByteBuffer.wrap(requestBytes, 0, 4).getInt();
        Integer placeholderId = ByteBuffer.wrap(requestBytes, 4, 4).getInt();

        // Track success and error message
        boolean success = true;
        String errorMessage = "";
        int entityId = -1;

        // Create a generic entity for the
        GenericEntity entity = null;

        try {
            // Load the class and construct the entity using that class
            Class<?> loadedClass = Class.forName(EntityTypeMap.getInstance().get(entityType));
            entity = (GenericEntity) loadedClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            // If there was an exception, we failed the registration
            success = false;
            errorMessage = "Error parsing register data for entity type: " + EntityTypeMap.getInstance().get(entityType) + "\nError: " + e.getMessage();
        }

        // If we succeeded, register the entity
        if (entity != null) {
            GenericEntityMap entityMap = GenericEntityMap.getInstance();
            entityId = entityMap.size();
            entityMap.put(Integer.toString(entityId), entity);
        }

//        System.out.println("EntityId: " + entityId + " PlaceholderId: " + placeholderId);
        handler.addEntity(entityId);

        // Compile and return the registerEntity result
        return compileRegisterResult(success, entityId, placeholderId, errorMessage);
    }

    /**
     * Compiles the result into a byte array to send back to the game engine.
     * If success is true, then the message is the type (int), an int with the number of bytes, a 1, signifying success, along
     * with two ints signifying the entityId and the placeholderId
     * Otherwise, the message is the type, an int with the number of bytes, a 0 (byte), and the bytes representing the
     * error message.
     * @param success Whether or not we were able to successfully setup the entity data we recieved
     * @param entityId The entity id
     * @param placeholderId The placeholder id given from the client
     * @param errorMessage Empty is success is true
     * @return The array of bytes to send back to the client
     */
    private byte[] compileRegisterResult(boolean success, int entityId, int placeholderId, String errorMessage) {
        // Setup the response, add the response type to it
        ArrayList<Byte> result = new ArrayList<>();
        result.add((byte) RequestType.ENTITY_REGISTER.getNumVal());
        result.add((byte)0);

        // If the setup was a success, then all we need is:
        // 1. length of message
        // 2. 1 byte set to 1 for success
        // 3. 2 ints (8 bytes) for placeholder and entity id
        if (success) {
            // Add the message length to our byte array
            // 1 for the success, 8 for the two ints that follow
            int messageLength = 2 + 8;
            ByteManager.addIntToByteArray(messageLength, result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);

            // Add the placeholderId and the entityId to the array
            ByteManager.addIntToByteArray(placeholderId, result);
            ByteManager.addIntToByteArray(entityId, result);
        }
        // Otherwise, send an error message
        else {
            // Add the integer 2 + errorMessage.getBytes().length to our byte array, this is the rest of the bytes
            int messageLength = 6 + errorMessage.getBytes().length;
            ByteManager.addIntToByteArray(messageLength, result);

            // Failure
            result.add((byte)0);
            result.add((byte)0);

            // Add the placeholder id
            ByteManager.addIntToByteArray(placeholderId, result);

            // Message
            ByteManager.addBytesToArray(errorMessage.getBytes(), result);
        }

        // Convert the arraylist into an array of bytes
        byte[] resultArray = ByteManager.convertArrayListToArray(result);

        return resultArray;
    }
}
