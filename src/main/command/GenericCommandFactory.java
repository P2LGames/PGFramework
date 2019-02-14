package main.command;

import command.GenericCommand;
import communication.ServerException;
import entity.GenericEntity;
import main.communication.request.CommandRequest;
import entity.GenericEntityMap;

/**
 * Class used to retrieve a command to be run
 */
public class GenericCommandFactory {

    /**
     * Retrieves the command that is defined by the supplied request or throws an exception
     *
     * @param request the request defining the command
     *
     * @return the generic command object defined by the request
     *
     * @throws ServerException throws when an non-existing entity ID is supplied
     */
    public GenericCommand getCommand(CommandRequest request) throws ServerException {
        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = entities.get(request.getEntityId());
        if(entity == null) {
            throw new ServerException("Invalid entity ID");
        }
        return entity.getCommand(request.getCommand());
    }

}
