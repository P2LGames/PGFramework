package main.communication.command;

/**
 * The request for a command from the client
 */
public class CommandRequest extends Request {

    public CommandRequest( String entityID, String command) {
        this.setEntityID(entityID);
        this.setCommand(command);
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
        return this.getEntityID().equals(that.getEntityID());
    }
}
