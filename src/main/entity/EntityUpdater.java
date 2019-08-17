package main.entity;

import annotations.Getter;
import annotations.Setter;
import command.GenericCommand;
import entity.GenericEntity;
import entity.GenericEntityMap;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Updates entity
 */
public class EntityUpdater {

    /**
     * Updates the supplied fields in the entity
     *
     * @param request
     *  the request holding the necessary info to update an entity
     *
     * @return
     *  the result of the entity being updated
     *
     */
//    public EntityUpdateResult updateEntity(EntityUpdateRequest request) {
//        EntityUpdateResult result;
//        // Get the entity via entityId
//        GenericEntity entity = GenericEntityMap.getInstance().get(request.getEntityId());
//
//        try {
//            // Get the entity's class
//            Class<?> entityClass = Class.forName(request.getEntityClass());
//            // Get the fields that we want to update
//            Map<String, Object> updates = request.getFieldsToUpdate();
//
//            // Loop through all of the methods in the entity's class
//            for(Method getterMethod : entityClass.getMethods()) {
//                // If there is a getter annotation present
//                if(getterMethod.isAnnotationPresent(Getter.class)) {
//                    // Then we get the field name
//                    String getterFieldName = getterMethod.getAnnotation(Getter.class).fieldName();
//                    // And check to see if our updates contains that key
//                    if(updates.containsKey(getterFieldName)) {
//                        // If it does, then get look for a setter for that method
//                        for(Method setterMethod : entityClass.getMethods()) {
//                            // If the setter exists
//                            if(setterMethod.isAnnotationPresent(Setter.class)) {
//                                // We get the field name for it
//                                String setterFieldName = setterMethod.getAnnotation(Setter.class).fieldName();
//                                // And ensure that the getter and setter field names are in sync
//                                if(getterFieldName.equals(setterFieldName)) {
//                                    // Then we run the set command with our passed in updates
//                                    Class<?> paramType = setterMethod.getAnnotation(Setter.class).type();
//                                    Class<?>[] params = new Class<?>[1];
//                                    params[0] = paramType;
//                                    GenericCommand setterCommand = new GenericCommand(entity, setterMethod, params);
//                                    setterCommand.setParameters(new Object[]{updates.get(setterFieldName)});
//                                    setterCommand.run();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            result = new EntityUpdateResult(request.getEntityId());
//        } catch (Exception e) {
//            result = new EntityUpdateResult(e.getMessage(), false);
//        }
//        return result;
//    }


}
