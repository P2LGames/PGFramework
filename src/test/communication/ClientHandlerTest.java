package test.communication;

import main.command.CommandError;
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
import java.util.Random;

import static org.junit.Assert.*;

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
            Thread.sleep(100);

            // Create a client socket
            client = new Socket();

            // Set it's timeout
            client.setSoTimeout(10000);

            // Connect to the server
            client.connect(new InetSocketAddress("localhost", ServerHandler.PORT));

            // Write a byte to the framework
            outToServer = new DataOutputStream(client.getOutputStream());

            // Read the data from the server
            inFromServer = new DataInputStream(client.getInputStream());

            Thread.sleep(100);
        }
        catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Close the client ins and outs
        try {
            if (client != null) {
                client.close();
            }
            if (inFromServer != null) {
                inFromServer.close();
            }
            if (outToServer != null) {
                outToServer.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Stop the server and in and outs
        server.stop();
    }

    @Test
    public void testClientHandler() {
        // Test the entity setup
        System.out.println("Testing Entity Setup");
        testEntitySetup();

        // Test registering an entity
        System.out.println("Testing Entity Register");
        testEntityRegister();

        // Test the command
        System.out.println("Testing Command");
        testCommand();

        // Wait just a bit
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {}

        // Test recompile commands
        System.out.println("Testing recompilation");
        testRecompile();

        // Test the command timeout
        System.out.println("Testing Command Timeout");
        testCommandTimeout();

        // Wait just a bit
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {}

        // Test sequential commands
        System.out.println("Testing Sequential Commands");
        testSequentialCommand();

        // Test asyncrous commands
        System.out.println("Testing two entities asyncrous");
        testTwoEntitiesAsync();



        // Wait just a bit
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {}

        // Check to make sure the entity exists before the client disconnects
//        assertTrue(GenericEntityMap.getInstance().entityExists("0"));

        // Disconnect the client
        try {
            client.close();
            inFromServer.close();
            outToServer.close();

            client = null;
            inFromServer = null;
            outToServer = null;

            // Wait for a few milliseconds for the client on the server side to drop
            Thread.sleep(200);
        }
        catch (IOException e) {
            System.out.println("Failed to close client socket: ");
            e.printStackTrace();
        }
        catch (InterruptedException e) {

        }

        // See if the client id still exists in the map
//        assertFalse(GenericEntityMap.getInstance().entityExists("0"));
    }

//    @Test
//    public void testRemoveEntitiesFromMap() {
//        // Test the entity setup
//        testEntitySetup();
//
//        // Test registering an entity
//        testEntityRegister();
//
//
//
//    }

    public void testEntitySetup() {
        // Create the entity data we will be sending
        byte[] entityData = "0:entity.TestEntity,1:entity.TestEntity2".getBytes();

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
            assertEquals(RequestType.ENTITY_SETUP.getNumVal(), type);
            assertEquals(2, length);
            assertEquals(1, success);
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testEntityRegister() {
        // Setup the request bytes
        ArrayList<Byte> registerBytes = new ArrayList<>();

        // Add the entity type and its placeholder id
        ByteManager.addIntToByteArray(0, registerBytes, true);
        ByteManager.addIntToByteArray(0, registerBytes, true);

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.ENTITY_REGISTER.getNumVal());

        // Add the length of the register bytes
        ByteManager.addIntToByteArray(registerBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(registerBytes), requestBytes);

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

            // Read in the placeholder and entity id
            int placeholderId = inFromServer.readInt();
            int entityId = inFromServer.readInt();

            // Check our asserts
            assertEquals(RequestType.ENTITY_REGISTER.getNumVal(), type);
            assertEquals(10, length);
            assertEquals(1, success);
            assertEquals(0, placeholderId);
            assertEquals(0, entityId);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }


        // Setup the request bytes
        registerBytes = new ArrayList<>();

        // Add the entity type and its placeholder id
        ByteManager.addIntToByteArray(0, registerBytes, true);
        ByteManager.addIntToByteArray(1, registerBytes, true);

        // Create some request bytes
        requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.ENTITY_REGISTER.getNumVal());

        // Add the length of the register bytes
        ByteManager.addIntToByteArray(registerBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(registerBytes), requestBytes);

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

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int placeholderId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Check our asserts
            assertEquals(RequestType.ENTITY_REGISTER.getNumVal(), type);
            assertEquals(10, length);
            assertEquals(1, success);
            assertEquals(1, placeholderId);
            assertEquals(1, entityId);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testCommand() {
        // Setup the request bytes
        ArrayList<Byte> commandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, commandBytes, true);
        ByteManager.addIntToByteArray(1, commandBytes, true);

        // There is no parameter
        commandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(commandBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(commandBytes), requestBytes);

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

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            String commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
            assertEquals(27, length);
            assertEquals(1, success);
            assertEquals(0, entityId);
            assertEquals(1, commandId);
            assertEquals("I can talk, yay!!", commandOutput);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testCommandTimeout() {
        // Setup the request bytes
        ArrayList<Byte> commandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, commandBytes, true);
        ByteManager.addIntToByteArray(2, commandBytes, true);

        // There is no parameter
        commandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(commandBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(commandBytes), requestBytes);

        try {
            // Send the request
            outToServer.write(ByteManager.convertArrayListToArray(requestBytes));

            // Get the type of responses
            System.out.println("Waiting for command to timeout...");
            int type = inFromServer.readByte();

            // Read a byte (It's a padding)
            inFromServer.readByte();

            // Get the length of the message
            byte[] lengthBytes = new byte[4];
            inFromServer.read(lengthBytes);
            int length = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read the success byte
            int errorType = inFromServer.readByte();

            // Read a buffer byte
            inFromServer.readByte();

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the error message
            byte[] errorMessageBytes = new byte[length - 10];
            inFromServer.read(errorMessageBytes);

            // Read in the rest of the bytes, they should be a string
            String errorMessage = new String(errorMessageBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND_ERROR.getNumVal(), type);
            assertEquals(CommandError.TIMEOUT.getNumVal(), errorType);
            assertEquals(0, entityId);
            assertEquals(2, commandId);
            assertEquals("Command took too long to finish, possible infinite loop." , errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testSequentialCommand() {
        // Setup the first request bytes
        ArrayList<Byte> firstCommandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, firstCommandBytes, true);
        ByteManager.addIntToByteArray(3, firstCommandBytes, true);

        // There is no parameter
        firstCommandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> firstRequestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(firstRequestBytes, 1);
        firstRequestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(firstCommandBytes.size(), firstRequestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(firstCommandBytes), firstRequestBytes);


        // Setup the second request bytes
        ArrayList<Byte> secondCommandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, secondCommandBytes, true);
        ByteManager.addIntToByteArray(4, secondCommandBytes, true);

        // There is no parameter
        secondCommandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> secondRequestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(secondRequestBytes, 1);
        secondRequestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(secondCommandBytes.size(), secondRequestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(secondCommandBytes), secondRequestBytes);

        try {
            // Send the two requests
            outToServer.write(ByteManager.convertArrayListToArray(firstRequestBytes));
            outToServer.write(ByteManager.convertArrayListToArray(secondRequestBytes));

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

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            String commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
            assertEquals(1, success);
            assertEquals(0, entityId);
            assertEquals(3, commandId);
            assertEquals("I ran first", commandOutput);

            // Get the type of responses
            type = inFromServer.readByte();

            // Read a byte (It's a padding)
            inFromServer.readByte();

            // Get the length of the message
            lengthBytes = new byte[4];
            inFromServer.read(lengthBytes);
            length = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read the success byte
            success = inFromServer.readByte();

            // Read a buffer byte
            inFromServer.readByte();

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
            assertEquals(1, success);
            assertEquals(0, entityId);
            assertEquals(4, commandId);
            assertEquals("I ran second", commandOutput);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testTwoEntitiesAsync() {
        // Setup the request bytes
        ArrayList<Byte> commandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, commandBytes, true);
        ByteManager.addIntToByteArray(5, commandBytes, true);

        // There is no parameter
        commandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(commandBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(commandBytes), requestBytes);


        // Setup the request bytes
        ArrayList<Byte> secondCommandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(1, secondCommandBytes, true);
        ByteManager.addIntToByteArray(6, secondCommandBytes, true);

        // There is no parameter
        secondCommandBytes.add((byte) 0);

        // Create some request bytes
        ArrayList<Byte> secondRequestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(secondRequestBytes, 1);
        secondRequestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(secondCommandBytes.size(), secondRequestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(secondCommandBytes), secondRequestBytes);

        try {
            // Send the request
            outToServer.write(ByteManager.convertArrayListToArray(requestBytes));
            outToServer.write(ByteManager.convertArrayListToArray(secondRequestBytes));

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

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            String commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
            assertEquals(1, success);
            assertEquals(1, entityId);
            assertEquals(6, commandId);
            assertEquals("I ran first", commandOutput);

            // Get the type of responses
            type = inFromServer.readByte();

            // Read a byte (It's a padding)
            inFromServer.readByte();

            // Get the length of the message
            lengthBytes = new byte[4];
            inFromServer.read(lengthBytes);
            length = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read the success byte
            success = inFromServer.readByte();

            // Read a buffer byte
            inFromServer.readByte();

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
            assertEquals(1, success);
            assertEquals(0, entityId);
            assertEquals(5, commandId);
            assertEquals("I ran last", commandOutput);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void testRecompile() {
        int i = new Random().nextInt();
        String testOverrideClass = "command.TestOverride";
        String testOverrideFilePath = "src/command/TestOverride";
        String testOverride = "package command;\n" +
                "\n" +
                "import annotations.SetEntity;\n" +
                "import annotations.Command;\n" +
                "import entity.TestEntity;\n\n" +
                "public class TestOverride {\n" +
                "    private TestEntity testEntity;\n" +
                "\n" +
                "    @Command(commandName = \"run\", id = 0)\n" +
                "    public void run() {\n" +
                "        int num = 0;\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"talk\", id = 1)\n" +
                "    public byte[] talk() {\n" +
                "        return \"I can talk, yay!! " + i + "\".getBytes();\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"timeout\", id = 2)\n" +
                "    public void timeout() {\n" +
                "        try {\n" +
                "            for (int i = 0; i < 10000; i++) {\n" +
                "                Thread.sleep(100);\n" +
                "            }\n" +
                "        }\n" +
                "        catch (InterruptedException e) {}\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"first\", id = 3)\n" +
                "    public byte[] first() {\n" +
                "        try {\n" +
                "            Thread.sleep(300);\n" +
                "        }\n" +
                "        catch (InterruptedException e) {}\n" +
                "\n" +
                "        return \"I ran first\".getBytes();\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"second\", id = 4)\n" +
                "    public byte[] second() {\n" +
                "        return \"I ran second\".getBytes();\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"asyncLast\", id = 5)\n" +
                "    public byte[] asyncLast() {\n" +
                "        try {\n" +
                "            Thread.sleep(300);\n" +
                "        }\n" +
                "        catch (InterruptedException e) {}\n" +
                "\n" +
                "        return \"I ran last\".getBytes();\n" +
                "    }\n" +
                "\n" +
                "    @Command(commandName = \"asyncFirst\", id = 6)\n" +
                "    public byte[] asyncFirst() {\n" +
                "        return \"I ran first\".getBytes();\n" +
                "    }\n" +
                "\n" +
                "    public TestEntity getTestEntity() {\n" +
                "        return testEntity;\n" +
                "    }\n" +
                "\n" +
                "    @SetEntity\n" +
                "    public void setTestEntity(TestEntity testEntity) {\n" +
                "        this.testEntity = testEntity;\n" +
                "    }\n" +
                "}";

        // Setup the request bytes
        ArrayList<Byte> recompileBytes = new ArrayList<>();

        // Add the entity id and the command id we want to recompile
        ByteManager.addIntToByteArray(0, recompileBytes, true);
        ByteManager.addIntToByteArray(1, recompileBytes, true);

        // Add the class and file contents
        ByteManager.addIntToByteArray(testOverrideFilePath.getBytes().length, recompileBytes, true);
        ByteManager.addBytesToArray(testOverrideFilePath.getBytes(), recompileBytes);
        ByteManager.addIntToByteArray(testOverrideClass.getBytes().length, recompileBytes, true);
        ByteManager.addBytesToArray(testOverrideClass.getBytes(), recompileBytes);
        ByteManager.addIntToByteArray(testOverride.getBytes().length, recompileBytes, true);
        ByteManager.addBytesToArray(testOverride.getBytes(), recompileBytes);

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.FILE_UPDATE.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(recompileBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(recompileBytes), requestBytes);

        try {
            // Send the request
            outToServer.write(ByteManager.convertArrayListToArray(requestBytes));

            // Read in the stuffs
            // Get the type of responses
            int type = inFromServer.readByte();

            // Read a byte (It's a padding)
            inFromServer.readByte();

            // Get the length of the message
            byte[] byteCount = new byte[4];
            inFromServer.read(byteCount);
            int length = ByteBuffer.wrap(byteCount, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read the success byte
            int success = inFromServer.readByte();

            // Read a buffer byte
            inFromServer.readByte();

            // Read in the placeholder and entity id
            inFromServer.read(byteCount);
            int entityId = ByteBuffer.wrap(byteCount, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(byteCount);
            int commandId = ByteBuffer.wrap(byteCount, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // We want the file path
            inFromServer.read(byteCount);
            int filePathLength = ByteBuffer.wrap(byteCount, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byteCount = new byte[filePathLength];
            inFromServer.read(byteCount);
            String filePath = new String(byteCount);

            // We want the class name
            byteCount = new byte[4];
            inFromServer.read(byteCount);
            int classNameLength = ByteBuffer.wrap(byteCount, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byteCount = new byte[classNameLength];
            inFromServer.read(byteCount);
            String classPackage = new String(byteCount);

            assertEquals(1, success);
            assertEquals("src/command/TestOverride", filePath);
            assertEquals("command.TestOverride", classPackage);

        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }



        // Setup the request bytes
        ArrayList<Byte> commandBytes = new ArrayList<>();

        // Add the entity id and the command id we want to run
        ByteManager.addIntToByteArray(0, commandBytes, true);
        ByteManager.addIntToByteArray(1, commandBytes, true);

        // There is no parameter
        commandBytes.add((byte) 0);

        // Create some request bytes
        requestBytes = new ArrayList<>();

        // Pad the left and add the request integer
        ByteManager.padWithBytes(requestBytes, 1);
        requestBytes.add((byte) RequestType.COMMAND.getNumVal());

        // Add the length of the command bytes
        ByteManager.addIntToByteArray(commandBytes.size(), requestBytes, true);

        // Add the register bytes to the request bytes
        ByteManager.addBytesToArray(ByteManager.convertArrayListToArray(commandBytes), requestBytes);

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

            // Read in the placeholder and entity id
            inFromServer.read(lengthBytes);
            int entityId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            inFromServer.read(lengthBytes);
            int commandId = ByteBuffer.wrap(lengthBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // Read in the rest of the bytes, they should be a string
            lengthBytes = new byte[length - 10];
            inFromServer.read(lengthBytes);
            String commandOutput = new String(lengthBytes);

            // Check our asserts
            assertEquals(RequestType.COMMAND.getNumVal(), type);
//            assertEquals(27, length);
            assertEquals(1, success);
            assertEquals(0, entityId);
            assertEquals(1, commandId);
            assertEquals("I can talk, yay!! " + i, commandOutput);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }

    }


}