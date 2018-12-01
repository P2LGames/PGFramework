package main.entity;

import command.Command;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Represents an in game entity that is programmable on the client
 */
public abstract class Entity {
    private String entityID;
    private Map<String, Constructor> constructorInstances;
    private Map<String, Class> parameterClassNames;

    Entity(String entityID) {
        this.entityID = entityID;
        this.constructorInstances = new HashMap<>();
        this.parameterClassNames = new HashMap<>();
    }

    Entity(String entityID, Map<String, Constructor> constructorInstances, Map<String, Class> parameterClassNames) {
        this.entityID = entityID;
        this.constructorInstances = constructorInstances;
        this.parameterClassNames = parameterClassNames;
    }

    /**
     * A command for getting a command, must be called from a concrete instance of the class
     * @param command
     *  the name of the command
     * @return
     *  the instance that implements the correct command interface
     */
    public abstract Command getCommand(String command, String serializedParameter);

    public abstract Command getCommand(String command);

    /**
     * Replaces the current instance for a class with another(if one already exists)
     *
     * @param commandName
     *  the name of the command
     * @param constructor
     *  the command that should replace the current one(if one exists)
     */
    public void replaceCommand(String commandName, Constructor constructor) {
        constructorInstances.put(commandName, constructor);
    }

    public String getEntityID() {
        return entityID;
    }

    public Map<String, Constructor> getConstructorInstances() {
        return constructorInstances;
    }

    public Map<String, Class> getParameterClassNames() {
        return parameterClassNames;
    }
}
