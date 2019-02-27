package test.entity;

import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;
import main.entity.EntityLoader;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    @Test
    public void testRegisterEntity() {
        EntityRequest request = new EntityRequest("entity.TestEntity", "1");
        EntityLoader loader = new EntityLoader();
        EntityResult result = loader.registerEntity(request);
        EntityResult expectedResult = new EntityResult("entity.TestEntity0", "1");
        assertEquals(result, expectedResult);

        EntityRequest request2 = new EntityRequest("entity.TestEntity", "2");
        EntityResult result2 = loader.registerEntity(request2);
        EntityResult expectedResult2 = new EntityResult("entity.TestEntity1", "2");
        assertEquals(result2, expectedResult2);

    }
}
