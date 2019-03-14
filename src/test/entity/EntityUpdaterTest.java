package test.entity;

import communication.ServerException;
import entity.GenericEntityMap;
import entity.TestEntity;
import main.communication.request.EntityUpdateRequest;
import main.entity.EntityUpdater;
import org.junit.Test;
import util.Serializer;

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

        TestEntity updateEntity = new TestEntity();
        entity.setRunSpeed(5);

        EntityUpdateRequest request = new EntityUpdateRequest();
        request.setSerializedEntity(Serializer.serialize(entity));
        request.setEntityClass("TestEntity");
        request.setEntityId("testEntity");

        EntityUpdater updater = new EntityUpdater();
        updater.updateEntity(request);

        assertEquals(5, entity.getRunSpeed());
    }


}
