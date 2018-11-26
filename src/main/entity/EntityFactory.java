package main.entity;

import main.communication.request.EntityRequest;

public class EntityFactory implements IEntityFactory {
    private static EntityFactory instance;
    private Integer idCount;

    static EntityFactory getInstance() {
        if(instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    private EntityFactory() {
        idCount = 0;
    }

    public Entity getNewEntity(EntityRequest request) {
        Entity entity = null;
        if(request.getEntityType().equals("testEntity")) {
            entity = new TestEntity("testEntity" + idCount);
        }
        idCount++;
        return entity;
    }
}
