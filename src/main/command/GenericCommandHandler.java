package main.command;

import command.Command;
import command.CommandResult;
import command.GenericCommand;
import communication.ServerException;
import main.communication.request.CommandRequest;

public class GenericCommandHandler {

    private GenericCommandFactory commandFactory;

    public GenericCommandHandler() {
        this.commandFactory = new GenericCommandFactory();
    }

    public void setCommandFactory(GenericCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

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
