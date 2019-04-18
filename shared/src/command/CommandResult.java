package command;

import java.util.Objects;

/**
 * The result of executing a command
 */
public class CommandResult extends Result {
    private String entityId;
    private Object value;

    public CommandResult() {
        super();
    }

    public CommandResult(String errorMessage, Boolean success) {
        super(errorMessage, success);
    }

    public CommandResult(Object value, String entityId) {
        this.entityId = entityId;
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
        return Objects.equals(this.getValue(), that.getValue());
    }

}
