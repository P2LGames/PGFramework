package main.communication.request;

/**
 * Super class of file and command requests
 */
public abstract class Request {
    private String entityId;
    private String command;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
