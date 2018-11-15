package main.communication.command;

/**
 * Super class of file and command requests
 */
public abstract class Request {
    private String entityID;
    private String command;

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
