package test.entity;

import communication.ServerException;
import entity.GenericEntityMap;
//import entity.TestEntity;
import main.entity.EntityUpdater;
import org.junit.Test;
import util.Serializer;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class EntityUpdaterTest {

//    @Test
//    public void testUpdateEntity() throws ServerException {
//        GenericEntityMap genericEntityMap = GenericEntityMap.getInstance();
//        TestEntity entity = new TestEntity();
//        entity.setRunSpeed(10);
//        entity.setxPos(10);
//        entity.setyPos(10);
//        genericEntityMap.put("testEntity", entity);
//
//
//        EntityUpdateRequest request = new EntityUpdateRequest();
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("runSpeed", 5);
//        request.setFieldsToUpdate(updates);
//        request.setEntityClass("entity.TestEntity");
//        request.setEntityId("testEntity");
//
//        EntityUpdater updater = new EntityUpdater();
//        updater.updateEntity(request);
//
//        assertEquals(5, entity.getRunSpeed());
//
//
//    }
//
//    @Test
//    public void testDeserializeEntityUpdateRequest() {
//        // Request Expected
//        EntityUpdateRequest requestExpected = new EntityUpdateRequest();
////        requestExpected.setEntityId("entity.Robot1");
////        requestExpected.setEntityClass("Robot");
////        HashMap<String, Object> map = new HashMap<>();
////        map.put("userInputMove", 0);
////        map.put("userInputRotate", 0);
////        requestExpected.setFieldsToUpdate(map);
//        // Serialized request
//        String serializedRequest = "{\"entityId\":\"entity.Robot1\",\"entityClass\":\"Robot\",\"fieldsToUpdate\":{\"userInputMove\":0,\"userInputRotate\":0}}";
//        EntityUpdateRequest request = Serializer.deserialize(serializedRequest, EntityUpdateRequest.class);
//
//        assertEquals("entity.Robot1", request.getEntityId());
//        assertEquals("Robot", request.getEntityClass());
//        assertEquals(0.0, request.getFieldsToUpdate().get("userInputMove"));
//        assertEquals(0.0, request.getFieldsToUpdate().get("userInputRotate"));
//
//
//    }


}
