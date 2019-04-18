package main.communication.request;

import java.util.Map;

/**
 * The request used for updating an entities data fields
 */
public class EntityUpdateRequest {
    private String entityId;
    private String entityClass;
    private Map<String, Object> fieldsToUpdate;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public Map<String, Object> getFieldsToUpdate() {
        return fieldsToUpdate;
    }

    public void setFieldsToUpdate(Map<String, Object> fieldsToUpdate) {
        this.fieldsToUpdate = fieldsToUpdate;
    }
}
