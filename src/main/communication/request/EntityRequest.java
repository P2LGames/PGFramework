package main.communication.request;

/**
 * The request used for registering a new entity
 */
public class EntityRequest {
    private String entityType;

    public EntityRequest(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }
}
