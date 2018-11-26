package test.entity;

import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;
import main.entity.EntityLoader;
import org.junit.Test;
import test.testutils.MockEntityFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    @Test
    public void testRegisterEntity() {
        EntityRequest request = new EntityRequest("testEntity");
        EntityLoader loader = new EntityLoader();
        loader.setFactory(new MockEntityFactory());
        EntityResult result = loader.registerEntity(request);
        List<String> expectedCommands = new ArrayList<>();
        expectedCommands.add("talk");
        EntityResult expectedResult = new EntityResult("testEntity0", expectedCommands);
        assertEquals(result, expectedResult);
    }
}
