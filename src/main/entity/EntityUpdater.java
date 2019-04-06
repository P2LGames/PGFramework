package main.entity;

import annotations.Getter;
import annotations.Setter;
import command.CommandResult;
import command.GenericCommand;
import entity.GenericEntity;
import entity.GenericEntityMap;
import main.communication.request.EntityUpdateRequest;
import main.communication.result.EntityUpdateResult;
import util.Serializer;

import java.lang.reflect.Method;
import java.util.Map;

public class EntityUpdater {

    public EntityUpdateResult updateEntity(EntityUpdateRequest request) {
        EntityUpdateResult result;
        GenericEntity entity = GenericEntityMap.getInstance().get(request.getEntityId());
        try {
            Class<?> entityClass = Class.forName(request.getEntityClass());
            Map<String, Object> updates = request.getFieldsToUpdate();
            for(Method getterMethod : entityClass.getMethods()) {
                if(getterMethod.isAnnotationPresent(Getter.class)) {
                    String getterFieldName = getterMethod.getAnnotation(Getter.class).fieldName();
                    if(updates.containsKey(getterFieldName)) {
                        for(Method setterMethod : entityClass.getMethods()) {
                            if(setterMethod.isAnnotationPresent(Setter.class)) {
                                String setterFieldName = setterMethod.getAnnotation(Setter.class).fieldName();
                                if(getterFieldName.equals(setterFieldName)) {
                                    Class<?> paramType = setterMethod.getAnnotation(Setter.class).type();
                                    Class<?>[] params = new Class<?>[1];
                                    params[0] = paramType;
                                    GenericCommand setterCommand = new GenericCommand(entity, setterMethod, params);
                                    setterCommand.setParameters(new Object[]{updates.get(setterFieldName)});
                                    setterCommand.run();
                                }
                            }
                        }
                    }
                }
            }
            result = new EntityUpdateResult(request.getEntityId());
        } catch (Exception e) {
            result = new EntityUpdateResult(e.getMessage(), false);
        }
        return result;
    }


}
