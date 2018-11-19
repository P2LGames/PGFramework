package main.communication.command;

import java.util.Objects;

public class CommandResult {
    private Object value;

    public CommandResult() {}

    public CommandResult(Object value) {
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
