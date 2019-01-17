package main.communication.request;

/**
 * The request for a command from the client
 */
public class CommandRequest extends Request {
    private Boolean hasParameter;
    private String serializedParameter;

    public CommandRequest( String entityID, String command, Boolean hasParameter, String serializedParameter) {
        this.setEntityId(entityID);
        this.setCommand(command);
        this.hasParameter = hasParameter;
        this.serializedParameter = serializedParameter;
    }

    public CommandRequest() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandRequest)) return false;
        CommandRequest that = (CommandRequest) o;
        if(!this.getCommand().equals(that.getCommand())) {
            return false;
        }
        return this.getEntityId().equals(that.getEntityId());
    }

    public Boolean getHasParameter() {
        return hasParameter;
    }

    public String getSerializedParameter() {
        return serializedParameter;
    }

    public void setHasParameter(Boolean hasParameter) {
        this.hasParameter = hasParameter;
    }
}
