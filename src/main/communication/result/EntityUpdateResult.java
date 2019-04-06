package main.communication.result;

import command.Result;

public class EntityUpdateResult extends Result {
    private String entityId;

    public EntityUpdateResult(String entityId) {
        this.entityId = entityId;
    }

    public EntityUpdateResult(String errorMessage, Boolean success) {
        super(errorMessage, success);
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
