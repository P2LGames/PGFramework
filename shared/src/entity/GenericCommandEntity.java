package entity;

import command.Command;
import command.GenericCommand;
import communication.ServerException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class GenericCommandEntity {
    private String entityID;
    private Map<String, GenericCommand> commandMap;

    GenericCommandEntity() {
        this.commandMap = new HashMap<>();
    }

    GenericCommandEntity(String entityID) {
        this.entityID = entityID;
        this.commandMap = new HashMap<>();
    }

    public String getEntityID() {
        return entityID;
    }

    public GenericCommand getCommand(String commandName) throws ServerException {
        try {
            return commandMap.get(commandName).clone();
        } catch (CloneNotSupportedException e) {
            throw new ServerException("Unable to clone command\n" + e.getMessage());
        }
    }

    public void updateCommand(String commandName, GenericCommand command) {
        commandMap.put(commandName, command);
    }

    public void makeDefault(String commandName, Method commandMethod) {
        GenericCommand command = new GenericCommand();
        command.setMethod(commandMethod);
        commandMap.put(commandName, command);
    }
}
