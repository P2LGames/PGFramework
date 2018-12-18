package main.command;

import command.Command;
import main.entity.EntityMap;
import main.communication.request.CommandRequest;
import entity.Entity;

/**
 * This class returns a command based on the request
 */
public class CommandFactory {

    /**
     * This method uses the request to return the correct command
     *
     * @param request
     *  the CommandRequest object to be processed
     *
     * @return
     *  the command that satisfies the request
     */
    public Command getCommand(CommandRequest request) throws CommandException {
        //Create the commandResult

        Command command = null;

        if(request.getHasParameter()) {
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            command = entity.getCommand(request.getCommand(), request.getSerializedParameter());

        }
        else {
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            command = entity.getCommand(request.getCommand());
        }
        if(command == null) {
            throw new CommandException("That entity cannot preform that action");
        }
        return command;
    }

}
