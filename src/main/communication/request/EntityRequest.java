package main.communication.request;

/**
 * The request used for registering a new entity
 */
public class EntityRequest {
    private String entityType;
    private String placeholderId;

    public EntityRequest(String entityType, String placeholderId) {
        this.entityType = entityType;
        this.placeholderId = placeholderId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getPlaceholderId() { return placeholderId; }
}
