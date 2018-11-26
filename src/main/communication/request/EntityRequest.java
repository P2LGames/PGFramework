package main.communication.request;

public class EntityRequest {
    private String entityType;

    public EntityRequest(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }
}
