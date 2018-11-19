package main.command;

import command.Command;
import main.command.Factory;
import main.communication.command.CommandRequest;
import main.communication.command.CommandResult;

/**
 * This class handles command requests given by the client
 */
public class CommandHandler {
    /**
     * Returns the result of the command returned by the command factory
     *
     * @param request
     *  the requests detailing the command to be run
     *
     * @param factory
     *  the factory that will retrieve the command
     *
     * @return
     *  the result of the command that is run
     */
    public CommandResult handleCommand(CommandRequest request, Factory factory) {
        CommandResult commandResult = new CommandResult();
        Command command = factory.route(request);
        Object commandOutput = command.run();
        commandResult.setValue(commandOutput);
        return commandResult;
    }
}
