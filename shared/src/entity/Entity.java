package entity;

import command.Command;
import util.Serializer;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Represents an in game entity that is programmable on the client
 */
public abstract class Entity {
    private String entityID;
    private Map<String, Constructor> constructorInstances;
    private Map<String, Class> parameterClassNames;

    Entity() {
        this.constructorInstances = new HashMap<>();
        this.parameterClassNames = new HashMap<>();
    }

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
     * @param commandName
     *  the name of the command
     * @return
     *  the instance that implements the correct command interface
     */
    public Command getCommand(String commandName, String serializedParameter) {
        Class<?> parameterClass = this.getParameterClassNames().get(commandName);
        Object parameter = Serializer.deserialize(serializedParameter, parameterClass);
        Constructor constructor = this.getConstructorInstances().get(commandName);
        Command command;
        try {
            command = (Command) constructor.newInstance(parameter);
        } catch (Exception e) {
            System.out.println("Fatal Error");
            return null;
        }
        return command;
    }

    public Command getCommand(String commandName) {
        Constructor constructor = this.getConstructorInstances().get(commandName);
        Command command;
        try {
            command = (Command) constructor.newInstance();
        } catch(Exception e) {
            System.out.println("Fatal Error");
            return null;
        }
        return command;
    }

    /**
     * Replaces the current instance for a class with another(if one already exists)
     *
     * @param commandName
     *  the name of the command
     * @param constructor
     *  the command that should replace the current one(if one exists)
     */
    public void replaceConstructor(String commandName, Constructor constructor) {
        constructorInstances.put(commandName, constructor);
    }

    /**
     * Creates the default for a command that does not take a parameter
     *
     * @param commandName
     *  the name of the command
     * @param commandClass
     *  the class object of the command class
     *
     * @throws NoSuchMethodException
     *  if the constructor requested does not exist
     */
    void makeDefault(String commandName, Class<?> commandClass) throws NoSuchMethodException {
        this.replaceConstructor(commandName, commandClass.getConstructor());
    }

    /**
     * Creates the default for a command that takes a parameter
     *
     * @param commandName
     *  the name of the command
     * @param commandClass
     *  the class object of the command class
     * @param parameterClass
     *  the class object of the parameter class
     *
     * @throws NoSuchMethodException
     *  if the constructor requested does not exist
     */
    void makeDefault(String commandName, Class<?> commandClass, Class<?> parameterClass) throws NoSuchMethodException {
        this.replaceConstructor(commandName, commandClass.getConstructor(parameterClass));
        parameterClassNames.put(commandName, parameterClass);
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
