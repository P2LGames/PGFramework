package test.entity;

import main.communication.RequestType;
import main.communication.ServerHandler;
import main.entity.EntityLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.ByteManager;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    ServerHandler server;

    @Before
    public void setup() {
        // Sets up and starts a server
        server = new ServerHandler();
        new Thread(server).start();
    }

    @After
    public void tearDown() {
        // Stop the server
        server.stop();
    }

//    @Test
//    public void testRegisterEntity() {
//        EntityRequest request = new EntityRequest("entity.TestEntity", "0");
//        EntityLoader loader = new EntityLoader();
//        EntityResult result = loader.registerEntity(request);
//        EntityResult expectedResult = new EntityResult("entity.TestEntity0",  "0");
//        assertEquals(result, expectedResult);
//
//        EntityRequest request2 = new EntityRequest("entity.TestEntity", "1");
//        EntityResult result2 = loader.registerEntity(request2);
//        EntityResult expectedResult2 = new EntityResult("entity.TestEntity1", "1");
//        assertEquals(result2, expectedResult2);
//
//    }

    @Test
    public void testSetupEntity() {
        // Create the entity data we will be sending
        byte[] entityData = "0:entity.TestEntity".getBytes();

        // Create some request bytes
        ArrayList<Byte> requestBytes = new ArrayList<>();

        // Pad the request with 1, 0
        ByteManager.padWithBytes(requestBytes, 1);

        // Add the request integer
        requestBytes.add((byte) RequestType.ENTITY_SETUP.getNumVal());

        // Add the length of the byte array to the
        ByteManager.addIntToByteArray(entityData.length, requestBytes);

        // Add the entity data as bytes to the request bytes
        ByteManager.addBytesToArray(entityData, requestBytes);

        // Send the request


    }


    @Test
    public void testRegisterEntity() {

    }
}
