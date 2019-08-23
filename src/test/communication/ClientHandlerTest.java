package test.communication;

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

import static org.junit.Assert.assertEquals;

public class ClientHandlerTest {

    ServerHandler server;

    Socket client;
    DataInputStream inFromServer;
    DataOutputStream outToServer;

    @Before
    public void setup() {
        // Sets up and starts a server
        server = new ServerHandler();
        new Thread(server).start();

        try {
            // Create a client socket
            client = new Socket();

            // Set it's timeout
            client.setSoTimeout(5000);

            // Connect to the server
            client.connect(new InetSocketAddress("localhost", ServerHandler.PORT));

            // Write a byte to the framework
            outToServer = new DataOutputStream(client.getOutputStream());

            // Read the data from the server
            inFromServer = new DataInputStream(client.getInputStream());
        }
        catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Close the client ins and outs
        try {
            client.close();
            inFromServer.close();
            outToServer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Stop the server and in and outs
        server.stop();
    }

    @Test
    public void testEntitySetup() {
        // Create the entity data we will be sending
        byte[] entityData = "0:entity.TestEntity1,1:entity.TestEntity2".getBytes();

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the request with 1, 0
        ByteManager.padWithBytes(requestBytes, 1);

        // Add the request integer
        requestBytes.add((byte) RequestType.ENTITY_SETUP.getNumVal());

        // Add the length of the byte array to the
        ByteManager.addIntToByteArray(entityData.length, requestBytes, true);

        // Add the entity data as bytes to the request bytes
        ByteManager.addBytesToArray(entityData, requestBytes);

        try {
            // Send the request
            outToServer.write(ByteManager.convertArrayListToArray(requestBytes));

            // Get the type of responses
            int type = inFromServer.readByte();

            // Read a byte (It's a padding)
            inFromServer.readByte();

            // Get the length of the message
            byte[] lengthBytes = new byte[4];
            inFromServer.read(lengthBytes);
            int length = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read the success byte
            int success = inFromServer.readByte();

            // Read a buffer byte
            inFromServer.readByte();

            // Check our asserts
            assertEquals(0, type);
            assertEquals(2, length);
            assertEquals(1, success);
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }


    }

}