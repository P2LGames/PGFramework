package main.entity;

import entity.GenericEntity;
import entity.GenericEntityMap;
import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;

/**
 * Loads the entities into the server
 */
public class EntityLoader {

    /**
     * Creates an entity of the specified type, assigns it an id, registers it, and returns the id
     *
     * @param request
     *  holds the type of entity to be created
     * @return
     *  the data structure holding the success of the operation and the entity id if it was successful
     */
    public synchronized EntityResult registerEntity(EntityRequest request) {
        GenericEntity entity;
        try {
            Class<?> loadedClass = Class.forName(request.getEntityType());
            entity = (GenericEntity) loadedClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new EntityResult(false, e.getMessage());
        }
        GenericEntityMap entityMap = GenericEntityMap.getInstance();
        String entityId = getEntityId(request.getEntityType(), entityMap.size());
        entityMap.put(entityId, entity);
        return new EntityResult(entityId, request.getPlaceholderId());
    }

    /**
     * Returns the next entity id
     *
     * @return
     *  the id
     */
    private String getEntityId(String name, int idNum) {
        return name + idNum;
    }
}
