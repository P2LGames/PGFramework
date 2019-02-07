package main.command;

import command.GenericCommand;
import communication.ServerException;
import entity.GenericCommandEntity;
import main.communication.request.CommandRequest;
import main.entity.GenericEntityMap;

public class GenericCommandFactory {

    public GenericCommand getCommand(CommandRequest request) throws ServerException {
        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericCommandEntity entity = entities.get(request.getEntityId());
        if(entity == null) {
            throw new ServerException("Invalid entity ID");
        }
        return entity.getCommand(request.getCommand());
    }

}
