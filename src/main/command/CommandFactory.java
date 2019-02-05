package main.command;

import command.Command;
import communication.ServerException;
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
    public Command getCommand(CommandRequest request) throws ServerException {
        Command command;

        EntityMap entities = EntityMap.getInstance();
        Entity entity = entities.get(request.getEntityId());
        if(entity == null) {
            throw new ServerException("Invalid Entity ID");
        }
        if(request.getHasParameter()) {
            command = entity.getCommand(request.getCommand(), request.getSerializedParameter());
        }
        else {
            command = entity.getCommand(request.getCommand());
        }
        return command;
    }

}
