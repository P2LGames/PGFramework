package main.entity;

import command.Command;
import main.communication.request.CommandData;
import main.communication.request.EntityRequest;
import main.communication.result.EntityResult;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EntityLoader {
    private IEntityFactory factory;

    public EntityLoader() {
        this.factory = EntityFactory.getInstance();
    }

    public EntityResult registerEntity(EntityRequest request) {
        Entity entity = factory.getNewEntity(request);
        try {
            addCommandDefaults(entity, request.getCommandsMap());
        } catch (Exception e) {
            return new EntityResult(false, e.getMessage());
        }
        return new EntityResult(entity.getEntityID());
    }

    private void addCommandDefaults(Entity entity, Map<String, CommandData> commandsMap) throws ClassNotFoundException, NoSuchMethodException {
        Set<Entry<String, CommandData>> entrySet = commandsMap.entrySet();
        for(Entry<String, CommandData> entry : entrySet) {
            CommandData data = entry.getValue();
            Class<?> commandClassObject = Class.forName(data.getCommandName());
            Constructor constructor;
            if(data.getHasParameter()) {
                Class<?> parameterClassObject = Class.forName(data.getParameterClassName());
                constructor = commandClassObject.getDeclaredConstructor(parameterClassObject);
            }
            else {
                constructor = commandClassObject.getConstructor();
            }
            entity.replaceCommand(entry.getKey(), constructor);
        }
    }

    public void setFactory(IEntityFactory factory) {
        this.factory = factory;
    }
}
