package main.command;

import command.Command;
import main.communication.request.CommandRequest;
import command.CommandResult;

import java.lang.reflect.Constructor;

/**
 * This class handles command requests given by the client
 */
public class CommandHandler {

    private CommandFactory commandFactory;

    public CommandHandler() {
        this.commandFactory = new CommandFactory();
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Returns the result of the command returned by the command ICommandFactory
     *
     * @param request
     *  the requests detailing the command to be run
     *
     * @return
     *  the result of the command that is run
     */
    public CommandResult handleCommand(CommandRequest request) {
        Command command;
        try {
            command = commandFactory.getCommand(request);
        } catch (CommandException e) {
            return new CommandResult(e.getMessage(), false);
        }
        return command.run();
    }
}
