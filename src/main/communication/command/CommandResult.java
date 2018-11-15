package main.communication.command;

import java.util.Objects;

public class CommandResult extends Request {
    private Object value;

    public CommandResult() {}

    public CommandResult(String entityID, String command, Object value) {
        this.setEntityID(entityID);
        this.setCommand(command);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandResult)) return false;
        CommandResult that = (CommandResult) o;
        if(!Objects.equals(this.getValue(), that.getValue())) {
            return false;
        }
        if(!this.getCommand().equals(that.getCommand())) {
            return false;
        }
        return this.getEntityID().equals(that.getEntityID());
    }

}
