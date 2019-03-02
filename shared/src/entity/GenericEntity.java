package entity;

import annotations.Command;
import annotations.Entity;
import command.GenericCommand;
import communication.ServerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class representing a reprogrammable entity
 */
public abstract class GenericEntity {
    private String entityID;
    private Map<String, GenericCommand> commandMap;
    private List<String> commandClasses;

    GenericEntity() throws ServerException {
        this.commandMap = new HashMap<>();
        this.commandClasses = new ArrayList<>();
        makeDefaults();
    }

    GenericEntity(String entityID) throws ServerException {
        this.entityID = entityID;
        this.commandMap = new HashMap<>();
        this.commandClasses = new ArrayList<>();
        makeDefaults();
    }

    public String getEntityID() {
        return entityID;
    }

    /**
     * Gets the command with the name provided
     *
     * @param commandName the name of the command to be retrieved
     *
     * @return the generic command object described by the name
     *
     * @throws ServerException thrown when a deep copy of the command is unable to be made
     */
    public GenericCommand getCommand(String commandName) throws ServerException {
        try {
            return commandMap.get(commandName).clone();
        } catch (CloneNotSupportedException e) {
            throw new ServerException("Unable to clone command\n" + e.getMessage());
        }
    }

    /**
     * Updates the current implementation of a command
     *
     * @param commandName the name of the command to be updated
     * @param command the generic command instance to update it to
     */
    public void updateCommand(String commandName, GenericCommand command) {
        commandMap.put(commandName, command);
    }

    /**
     * Makes a default implementation for a command
     */
    private void makeDefaults() throws ServerException {

        Class<?> thisClass = this.getClass();
        if(!thisClass.isAnnotationPresent(Entity.class)) {
            throw new ServerException("This entity is missing the @Entity annotation");
        }
        else {
            Class<?> defaultCommandsClass = thisClass.getAnnotation(Entity.class).defaultCommands();
            for (Method commandMethod : defaultCommandsClass.getDeclaredMethods()) {
                if (commandMethod.isAnnotationPresent(Command.class)) {
                    String commandName = commandMethod.getAnnotation(Command.class).commandName();
                    GenericCommand command = new GenericCommand();
                    command.setMethod(commandMethod);
                    commandMap.put(commandName, command);
                    addCommandClass(commandMethod.getDeclaringClass().getName());
                }
            }
        }
    }

    public void addCommandClass(String className) {
        commandClasses.add(className);
    }

    public List<String> getCommandClasses() { return commandClasses; }
}
