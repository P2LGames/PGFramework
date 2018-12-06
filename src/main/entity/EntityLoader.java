package main.entity;

import entity.Entity;
import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;

/**
 * Loads the entities into the server
 */
public class EntityLoader {
    private Integer idCount;

    public EntityLoader() {
        idCount = 0;
    }

    /**
     * Creates an entity of the specified type, assigns it an id, registers it, and returns the id
     *
     * @param request
     *  holds the type of entity to be created
     * @return
     *  the data structure holding the success of the operation and the entity id if it was successful
     */
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

    /**
     * Returns the next entity id
     *
     * @return
     *  the id
     */
    private String getEntityId() {
        String id = Entity.class.toString() + idCount;
        idCount++;
        return id;
    }
}
