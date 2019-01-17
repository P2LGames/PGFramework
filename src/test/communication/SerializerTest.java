package test.communication;

import main.communication.request.CommandRequest;
import org.junit.Test;
import util.Serializer;

import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void testSerialize(){
        //Test command request serialization
        CommandRequest request = new CommandRequest();
        request.setEntityId("testID");
        request.setCommand("testCommand");
        String serializerRequestString = Serializer.serialize(request);
        String actualRequestString = "{\"entityID\":\"testID\",\"command\":\"testCommand\"}";
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
        String requestToDeserialize = "{\"entityID\":\"testID\",\"command\":\"testCommand\"}";
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
    }

}