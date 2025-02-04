package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ByteManager {

    /**
     * Turns an integer into an array of bytes in LITTLE_ENDIAN format.
     * @param toConvert
     * @return The byte array representing the integer
     */
    public static byte[] convertIntToByteArray(int toConvert, Boolean... bigEndian) {
        // Get the byte array of the int and return it, little endian style
        if (bigEndian.length > 0 && bigEndian[0]) {
            return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(toConvert).array();
        }
        else {
            return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(toConvert).array();
        }
    }

    public static void addIntToByteArray(int toConvert, ArrayList<Byte> array) {
        addBytesToArray(convertIntToByteArray(toConvert, false), array);
    }

    public static void addIntToByteArray(int toConvert, ArrayList<Byte> array, boolean bigEndian) {
        addBytesToArray(convertIntToByteArray(toConvert, bigEndian), array);
    }

    /**
     * Turns a float into an array of bytes in LITTLE_ENDIAN format.
     * @param toConvert
     * @return The byte array representing the float
     */
    public static byte[] convertFloatToByteArray(float toConvert) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(toConvert).array();
    }

    public static void addFloatToByteArray(float toConvert, ArrayList<Byte> array) {
        addBytesToArray(convertFloatToByteArray(toConvert), array);
    }

    /**
     * Turns a double into an array of bytes in LITTLE_ENDIAN format.
     * @param toConvert
     * @return The byte array representing the float
     */
    public static byte[] convertDoubleToByteArray(double toConvert) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(toConvert).array();
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

    /**
     * Pads the given array list with the given number of 0s
     * @param array The array list to pad
     * @param padCount The number of desired 0s
     */
    public static void padWithBytes(ArrayList<Byte> array, int padCount) {
        for (int i = 0; i < padCount; i++) {
            array.add((byte)0);
        }
    }

}
