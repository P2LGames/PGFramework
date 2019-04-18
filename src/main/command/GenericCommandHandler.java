package main.command;

import command.CommandResult;
import command.GenericCommand;
import communication.ServerException;
import main.communication.request.CommandRequest;

/**
 * The class that handles the running of a generic command
 */
public class GenericCommandHandler {

    private GenericCommandFactory commandFactory;

    public GenericCommandHandler() {
        this.commandFactory = new GenericCommandFactory();
    }

    public void setCommandFactory(GenericCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Takes a command request and using the factory to retrieve the correct command and run it
     *
     * @param request the request holding the data for the command to be run
     *
     * @return the result of running the command
     */
    public CommandResult handleCommand(CommandRequest request) {
        GenericCommand command;
        try {
            command = commandFactory.getCommand(request);
        } catch(ServerException e) {
            return new CommandResult(e.getMessage(), false);
        }
        command.setParameters(request.getParameters());
        return command.run();
    }
}
