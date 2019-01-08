package test.entity;

import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;
import main.entity.EntityLoader;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityLoaderTest {

    @Test
    public void testRegisterEntity() {
        System.out.println(System.getProperty("os.name"));
        EntityRequest request = new EntityRequest("entity.TestEntity");
        EntityLoader loader = new EntityLoader();
        EntityResult result = loader.registerEntity(request);
        EntityResult expectedResult = new EntityResult("entity.TestEntity0");
        assertEquals(result, expectedResult);
    }
}
