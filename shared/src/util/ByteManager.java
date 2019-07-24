package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ByteManager {

    /**
     * Turns an integer into an array of bytes in LITTLE_ENDIAN format.
     * @param toConvert
     * @return The byte array representing and integer
     */
    public static byte[] convertIntToByteArray(int toConvert) {
        // Get the byte array of the int and return it, little endian style
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(toConvert).array();
    }

    /**
     * Adds the given bytes to the passed ArrayList.
     * @param bytes The bytes to add to the array list
     * @param array The array list of bytes
     */
    public static void addBytesToArray(byte[] bytes, ArrayList<Byte> array) {
        // Add each byte to the array
        // Array is an object, so passed by reference
        for (Byte b: bytes) {
            array.add(b);
        }
    }

    public static void addIntToByteArray(int toConvert, ArrayList<Byte> array) {
        addBytesToArray(convertIntToByteArray(toConvert), array);
    }

    /**
     * Converts the given ArrayList of Bytes into a primitive byte array.
     * @param bytes
     * @return
     */
    public static byte[] convertArrayListToArray(ArrayList<Byte> bytes) {
        // Define an array of size of bytes in array list
        byte[] resultBytes = new byte[bytes.size()];

        // Loop through the array list and add each byte to our primitive array
        for (int i = 0; i < bytes.size(); i++) {
            resultBytes[i] = bytes.get(i);
        }

        // Return the primitive array
        return resultBytes;
    }

}
