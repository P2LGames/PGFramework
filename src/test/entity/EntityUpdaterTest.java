package test.entity;

import communication.ServerException;
import entity.GenericEntityMap;
import entity.TestEntity;
import main.communication.request.EntityUpdateRequest;
import main.entity.EntityUpdater;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class EntityUpdaterTest {

    @Test
    public void testUpdateEntity() throws ServerException {
        GenericEntityMap genericEntityMap = GenericEntityMap.getInstance();
        TestEntity entity = new TestEntity();
        entity.setRunSpeed(10);
        entity.setxPos(10);
        entity.setyPos(10);
        genericEntityMap.put("testEntity", entity);

<<<<<<< HEAD
        TestEntity updateEntity = new TestEntity();
//        entity.setRunSpeed(5);

=======
>>>>>>> f788252fe48c508419bf4b124b223fa466e98958
        EntityUpdateRequest request = new EntityUpdateRequest();
        Map<String, Object> updates = new HashMap<>();
        updates.put("runSpeed", 5);
        request.setFieldsToUpdate(updates);
        request.setEntityClass("entity.TestEntity");
        request.setEntityId("testEntity");

        EntityUpdater updater = new EntityUpdater();
        updater.updateEntity(request);

        assertEquals(5, entity.getRunSpeed());
    }


}
