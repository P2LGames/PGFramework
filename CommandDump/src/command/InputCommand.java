package command;

import command.Command;
import command.CommandResult;

public abstract class InputCommand implements Command {
    @Override
    public CommandResult run() {
        return new CommandResult(runOnInput());
    }

    public abstract <T> T runOnInput();

}
