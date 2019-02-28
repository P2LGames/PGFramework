package main.communication.request;

public class EntityUpdateRequest {
    private String entityId;
    private String entityClass;
    private String serializedEntity;

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

    public String getSerializedEntity() {
        return serializedEntity;
    }

    public void setSerializedEntity(String serializedEntity) {
        this.serializedEntity = serializedEntity;
    }
}
