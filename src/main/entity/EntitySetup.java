package main.entity;

import main.communication.RequestType;
import util.ByteManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EntitySetup {

    /**
     * Sets up the server with the given entityIds and their types.
     * @param bytes An array of bytes representing a string in id:className,id:className format.
     * @return The byte array to send back to the client, letting it know if we failed or succeeded.
     */
    public byte[] setupEntitiesWithBytes(EntityTypeMap entityTypeMap, byte[] bytes) {
        boolean success = true;
        String errorMessage = "";

        // Turn the data into a string
        String entitySetupData = new String(bytes, StandardCharsets.US_ASCII);

        // Split the string and use it to get information to the entity type map
        String[] types = entitySetupData.split(",");

        // Make sure we catch exceptions in the setup
        try {
            // Loop through each type and split it again to get the integer and it's type
            for (String type : types) {
                // Unpack the id and the class name from the type
                String[] typeToClass = type.split(":");
                int entityType = Integer.parseInt(typeToClass[0]);
                String entityClass = typeToClass[1];

                // Add it to our entity type map for future use
                entityTypeMap.put(entityType, entityClass);
                System.out.println(entityTypeMap.get(0));
            }
        }
        catch (IndexOutOfBoundsException e) {
            // Exception means that parsing the string didn't go so well
            success = false;
            // Setup the error message properly
            errorMessage = "Error parsing setup data: " + e.getMessage();
        }

        return compileSetupResult(success, errorMessage);
    }

    /**
     * Compiles the result into a byte array to send back to the server.
     * If success is true, then the message is the type, an int with the number of bytes, and a 1, signifying success.
     * Otherwise, the message is the type, an int with the number of bytes, a 0 (byte), and the bytes representing the
     * error message.
     * @param success Whether or not we were able to successfully setup the entity data we recieved
     * @param errorMessage Empty is success is true
     * @return The array of bytes to send back to the client
     */
    private byte[] compileSetupResult(boolean success, String errorMessage) {
        // Our result is a list of bytes
        ArrayList<Byte> result = new ArrayList<>();

        // Add the setup id to the result
        // 1. Setup the response type short
        result.add((byte) RequestType.ENTITY_SETUP.getNumVal());
        result.add((byte)0);
        // If the setup was a success, then all we need is:
        // 1. A 1 for one byte after
        // 2. A byte set to 1, specifying that is was a success
        if (success) {
            // Add the integer 1 to our byte array, only 2 bytes to send: Success (1) plus 0 (padding)
            ByteManager.addBytesToArray(ByteManager.convertIntToByteArray(2), result);

            // Add that is was a success
            result.add((byte)1);
            result.add((byte)0);
        }
        else {
            // Add the integer 2 + errorMessage.getBytes().length to our byte array, this is the rest of the bytes
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
