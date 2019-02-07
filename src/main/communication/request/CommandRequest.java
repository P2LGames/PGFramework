package main.communication.request;

/**
 * The request for a command from the client
 */
public class CommandRequest extends Request {
    private Object[] parameters;

    public CommandRequest( String entityID, String command) {
        this.setEntityId(entityID);
        this.setCommand(command);
    }

    public CommandRequest( String entityID, String command, Object[] parameters) {
        this.setEntityId(entityID);
        this.setCommand(command);
        this.parameters = parameters;
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

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
