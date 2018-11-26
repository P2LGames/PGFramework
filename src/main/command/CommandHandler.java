package main.command;

import command.Command;
import main.communication.request.CommandRequest;
import main.communication.result.CommandResult;

/**
 * This class handles command requests given by the client
 */
public class CommandHandler {
    /**
     * Returns the result of the command returned by the command ICommandFactory
     *
     * @param request
     *  the requests detailing the command to be run
     *
     * @param ICommandFactory
     *  the ICommandFactory that will retrieve the command
     *
     * @return
     *  the result of the command that is run
     */
    public CommandResult handleCommand(CommandRequest request, ICommandFactory ICommandFactory) {
        CommandResult commandResult = new CommandResult();
        Command command = ICommandFactory.getCommand(request);
        Object commandOutput = command.run();
        commandResult.setValue(commandOutput);
        return commandResult;
    }
}
