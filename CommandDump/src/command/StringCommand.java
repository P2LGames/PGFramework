package command;

import command.Command;
import command.CommandResult;

/**
 * The abstract class for a talk command
 */
public abstract class StringCommand implements Command {
    public abstract String getString();

    @Override
    public CommandResult run() {
        return new CommandResult(getString());
    }
}
