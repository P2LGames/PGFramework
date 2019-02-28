package main.entity;

import entity.GenericEntity;
import main.communication.request.EntityUpdateRequest;
import main.communication.result.EntityUpdateResult;
import util.Serializer;

public class EntityUpdater {

    public EntityUpdateResult updateEntity(EntityUpdateRequest request) {
        EntityUpdateResult result = new EntityUpdateResult();
        try {
            Class<?> entityClass = Class.forName(request.getEntityClass());
            if(GenericEntity.class.isAssignableFrom(entityClass)) {
                Class<? extends GenericEntity> entityClassNew = (Class<? extends GenericEntity>) entityClass;
                GenericEntity entity = Serializer.deserialize(request.getSerializedEntity(), entityClassNew);
                //TODO figure out how to programatically update this object
            }
            return null;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }


}
