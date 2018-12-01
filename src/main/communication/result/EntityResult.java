package main.communication.result;

import java.util.Objects;

public class EntityResult {
    private String entityId;
    private Boolean success;
    private String errorMessage;

    public EntityResult(String entityId) {
        this.success = true;
        this.entityId = entityId;
    }

    public EntityResult(Boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getEntityId() {
        return entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityResult)) return false;
        EntityResult that = (EntityResult) o;
        return Objects.equals(entityId, that.entityId);
    }
}
