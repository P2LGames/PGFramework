package command;

import command.Command;
import command.CommandResult;

/**
 * The abstract class for a talk command
 */
public abstract class StringCommand extends GenericCommand {

    public abstract String getString();

    @Override
    public CommandResult run() {
        return new CommandResult(getString());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
