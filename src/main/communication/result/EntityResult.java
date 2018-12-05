package main.communication.result;

import java.util.Objects;



public class EntityResult extends Result {
    private String entityId;

    public EntityResult(String entityId) {
        super(null, true);
        this.entityId = entityId;
    }

    public EntityResult(Boolean success, String errorMessage) {
        super(errorMessage, success);
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
