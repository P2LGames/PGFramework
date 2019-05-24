package command;

import java.util.Objects;

/**
 * The result of executing a command
 */
public class CommandResult extends Result {
    private String entityId;
    private String command;
    private Object value;

    public CommandResult() {
        super();
    }

    public CommandResult(String errorMessage, Boolean success, String entityId) {
        super(errorMessage, success);
        this.entityId = entityId;
    }

    public CommandResult(Object value, String entityId) {
        this.entityId = entityId;
        this.value = value;
    }

    public String getEntityId() { return entityId; }

    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getCommand() { return command; }

    public void setCommand(String command) { this.command = command; }

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
        return Objects.equals(this.getValue(), that.getValue());
    }

}
