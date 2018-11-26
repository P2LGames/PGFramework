package main.entity;

import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;

import java.util.List;

public class EntityLoader {
    private IEntityFactory factory;

    public EntityLoader() {
        this.factory = EntityFactory.getInstance();
    }

    public EntityResult registerEntity(EntityRequest request) {
        Entity entity = factory.getNewEntity(request);
        List<String> commandList = entity.getCommandsAsStringList();
        return new EntityResult(entity.getEntityID(), commandList);
    }

    public void setFactory(IEntityFactory factory) {
        this.factory = factory;
    }
}
