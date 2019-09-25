package test;

import main.communication.ClientHandler;
import main.communication.RequestType;
import main.communication.ServerHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ByteManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class ThreadInterruptTest {

    InfiniteLoopThread infLoop;


    public void setup() {
        infLoop = new InfiniteLoopThread();
        infLoop.start();
    }


    public void tearDown() {
        infLoop = null;
    }


    public void testInterrupt() {
        // Ensure the thread is alive
        assertTrue(infLoop.isAlive());

        // Stop the thread
        infLoop.stop();

        // Wait for 1 milli
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {

        }

        // Check to see if is alive
        assertFalse(infLoop.isAlive());
    }


    private class InfiniteLoopThread extends Thread {

        @Override
        public void run() {
            while (true) {
                System.out.println("Still looping");
            }
        }
    }

}