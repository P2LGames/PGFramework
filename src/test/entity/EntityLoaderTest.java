package test.entity;

import main.communication.request.CommandData;
import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;
import main.entity.EntityLoader;
import org.junit.Test;
import test.testutils.MockEntityFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    @Test
    public void testRegisterEntity() {
        Map<String, CommandData> commandsMap = new HashMap<>();
        CommandData data = new CommandData("command.StringCommandDefault", false, null);
        commandsMap.put("talk", data);
        EntityRequest request = new EntityRequest("testEntity", commandsMap);
        EntityLoader loader = new EntityLoader();
        loader.setFactory(new MockEntityFactory());
        EntityResult result = loader.registerEntity(request);
        EntityResult expectedResult = new EntityResult("testEntity0");
        assertEquals(result, expectedResult);
    }
}
