package test.utils;

import org.junit.Test;
import util.ByteManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteManagerTest {

    @Test
    public void testDoubleBytes() {
        double test1 = 355.543;
        double test2 = 1555.012345;

        System.out.println("Test 1");
        byte[] test1b = ByteManager.convertDoubleToByteArray(test1);
        for (int i = 0; i < 8; i++) {
            if ((int)test1b[i] < 0) test1b[i] = (byte)(test1b[i] + 256);
            System.out.println((int)test1b[i]);
        }
        System.out.println("Test 2");
        byte[] test2b = ByteManager.convertDoubleToByteArray(test2);
        for (int i = 0; i < 8; i++) {
            if (test2b[i] < 0) test2b[i] = (byte)(test2b[i] + 256);
            System.out.println(test2b[i]);
        }

        byte[] test3b = new byte[]{ (byte)(64), (byte)(118), (byte)(56), (byte)(-80 + 256), (byte)(32), (byte)(-60 + 256), (byte)(-101 + 256), (byte)(-90 + 256) };

        System.out.println(ByteBuffer.wrap(test1b).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        System.out.println(ByteBuffer.wrap(test3b).getDouble());
        System.out.println(ByteBuffer.wrap(test2b).order(ByteOrder.LITTLE_ENDIAN).getDouble());

    }

}
