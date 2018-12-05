package main.entity;

import entity.Entity;
import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;

public class EntityLoader {
    private Integer idCount;

    public EntityLoader() {
        idCount = 0;
    }

    public EntityResult registerEntity(EntityRequest request) {
        Entity entity;
        try {
            Class<?> loadedClass = Class.forName(request.getEntityType());
            entity = (Entity) loadedClass.newInstance();
        } catch (Exception e) {
            System.out.println("Fatal error");
            return new EntityResult(false, e.getMessage());
        }
        String entityId = getEntityId();
        EntityMap entityMap = EntityMap.getInstance();
        entityMap.put(entityId, entity);
        return new EntityResult(entityId);
    }

    public String getEntityId() {
        String id = Entity.class.toString() + idCount;
        idCount++;
        return id;
    }
}
