package test.entity;

import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;
import main.entity.EntityLoader;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    @Test
    public void testRegisterEntity() {
<<<<<<< HEAD
        EntityRequest request = new EntityRequest("entity.TestEntity", "0");
        EntityLoader loader = new EntityLoader();
        EntityResult result = loader.registerEntity(request);
        EntityResult expectedResult = new EntityResult("entity.TestEntity0",  "0");
        assertEquals(result, expectedResult);

        EntityRequest request2 = new EntityRequest("entity.TestEntity", "1");
        EntityResult result2 = loader.registerEntity(request2);
        EntityResult expectedResult2 = new EntityResult("entity.TestEntity1", "1");
=======
        EntityRequest request = new EntityRequest("entity.TestEntity", "1");
        EntityLoader loader = new EntityLoader();
        EntityResult result = loader.registerEntity(request);
        EntityResult expectedResult = new EntityResult("entity.TestEntity0", "1");
        assertEquals(result, expectedResult);

        EntityRequest request2 = new EntityRequest("entity.TestEntity", "2");
        EntityResult result2 = loader.registerEntity(request2);
        EntityResult expectedResult2 = new EntityResult("entity.TestEntity1", "2");
>>>>>>> 32fb74ec918e485d1b2fc4a141fc5ceea62337c8
        assertEquals(result2, expectedResult2);

    }
}
