package test.communication;

import main.communication.request.CommandRequest;
import org.junit.Test;
import util.Serializer;

import java.util.HashMap;

import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void testSerialize(){
        //Test command request serialization
        CommandRequest request = new CommandRequest();
        request.setEntityId("testID");
        request.setCommand("testCommand");
        String serializerRequestString = Serializer.serialize(request);
        String actualRequestString = "{\"entityId\":\"testID\",\"command\":\"testCommand\"}";
        assertEquals(serializerRequestString, actualRequestString);

        //Test string
        String string = "testString";
        String serializerStringString = Serializer.serialize(string);
        String actualStringString = "\"testString\"";
        assertEquals(serializerStringString, actualStringString);

        //Test double
        Double num = 20.0;
        String serializerDoubleString = Serializer.serialize(num);
        String actualNumString = "20.0";
        assertEquals(serializerDoubleString, actualNumString);
    }

    @Test
    public void testDeserialize() {
        //Test command request deserialization
        CommandRequest request = new CommandRequest();
        request.setEntityId("testID");
        request.setCommand("testCommand");
        String requestToDeserialize = "{\"entityId\":\"testID\",\"command\":\"testCommand\"}";
        CommandRequest deserializedRequest = Serializer.deserialize(requestToDeserialize, CommandRequest.class);
        assertEquals(request, deserializedRequest);

        //Test string
        String string = "testString";
        String stringToDeserialize = "testString";
        String deserializedString = Serializer.deserialize(stringToDeserialize, String.class);
        assertEquals(deserializedString, string);

        //Test double
        Double num = 20.0;
        String numToDeserialize = "20.0";
        Double deserializedNum = Serializer.deserialize(numToDeserialize, Double.class);
        assertEquals(deserializedNum, num);

        //Test Map
        HashMap<String, Object> map = new HashMap<>();
        String mapToDeserialize = "{\"test1\":20.0, \"test2\":1, \"test3\":\"here\"}";
        map.put("test1", 20.0);
        map.put("test2", 1.0);
        map.put("test3", "here");
        HashMap<String, Object> deserializedMap = Serializer.deserialize(mapToDeserialize, HashMap.class);
        assertEquals(map, deserializedMap);
    }

}