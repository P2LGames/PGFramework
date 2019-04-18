package main.communication.result;

import command.Result;

import java.util.Objects;


/**
 * The result from registering a new entity
 */
public class EntityResult extends Result {
    private String entityId;
    private String placeholderId;

    public EntityResult(String entityId, String placeholderId) {
        super(null, true);
        this.entityId = entityId;
        this.placeholderId = placeholderId;
    }

    public EntityResult(Boolean success, String errorMessage) {
        super(errorMessage, success);
    }

    public String getEntityId() {
        return entityId;
    }
    public String getPlaceholderId() { return placeholderId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityResult)) return false;
        EntityResult that = (EntityResult) o;
        return Objects.equals(entityId, that.entityId) && Objects.equals(placeholderId, that.placeholderId);
    }
}
