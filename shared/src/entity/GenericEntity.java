package entity;

import annotations.Command;
import annotations.Entity;
import annotations.SetEntity;
import command.GenericCommand;
import communication.ServerException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The class representing a reprogrammable entity
 */
public abstract class GenericEntity {
    private String entityID;
    private Map<String, GenericCommand> commandMap;
    private Map<Integer, GenericCommand> commandMapId;
    private List<String> commandClasses;

    GenericEntity() throws ServerException {
        this.commandMap = new HashMap<>();
        this.commandMapId = new HashMap<>();
        this.commandClasses = new ArrayList<>();
        makeDefaults();
    }

    GenericEntity(String entityID) throws ServerException {
        this.entityID = entityID;
        this.commandMap = new HashMap<>();
        this.commandMapId = new HashMap<>();
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
    public GenericCommand getCommand(int commandId) throws ServerException {
        try {
            return commandMapId.get(commandId).clone();
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

    public void updateCommand(Integer commandId, GenericCommand command) { commandMapId.put(commandId, command); }

    /**
     * Makes a default implementation for a command
     */
    private void makeDefaults() throws ServerException {

        try {
            // Get this class
            Class<?> thisClass = this.getClass();

            // If there is no entity annotation
            if(!thisClass.isAnnotationPresent(Entity.class)) {
                // Then we throw an error
                throw new ServerException("This entity is missing the @Entity annotation");
            }
            // Otherwise, we setup the default commands
            else {
                // Get the default command class
                Class<?> defaultCommandsClass = thisClass.getAnnotation(Entity.class).defaultCommands();

                // Setup the commands with the default class
                setupCommandsWithClass(defaultCommandsClass);
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

    }

    public void setupCommandsWithClass(Class<?> commandsClass) throws Exception {
        // Create an object with it
        Object classObject = commandsClass.getConstructor().newInstance();

        // Loop through the methods is contains
        for (Method commandMethod : commandsClass.getDeclaredMethods()) {
            // If an have the command annotation, then set that method up as a command
            if (commandMethod.isAnnotationPresent(Command.class)) {

                // Get the command name and id for the maps
                String commandName = commandMethod.getAnnotation(Command.class).commandName();
                Integer commandId = commandMethod.getAnnotation(Command.class).id();

                // Create a generic command
                GenericCommand command = new GenericCommand();

                // Set the class object and method of the command
                command.setClassObject(classObject);
                command.setMethod(commandMethod);

                // Add the command to the maps
                commandMap.put(commandName, command);
                commandMapId.put(commandId, command);

                // Add the command class to our list
                addCommandClass(commandMethod.getDeclaringClass().getName());
            }
            else if (commandMethod.isAnnotationPresent(SetEntity.class)) {
                // Set the entity of our class object
                commandMethod.invoke(classObject, this);
            }
        }
    }

    public void setupCommandIdWithClass(Class<?> commandsClass, int commandId) throws Exception {
        // Create a class object with the loaded class
        Object classObject = commandsClass.getConstructor().newInstance();

        // Create a generic command
        GenericCommand command = new GenericCommand();

        // Loop through the methods the commands class has
        for (Method commandMethod : commandsClass.getDeclaredMethods()) {
            // If it has the command annotation, then check to see if it has the commandId
            if (commandMethod.isAnnotationPresent(Command.class)) {

                // Get the command name and id for the maps
                int methodId = commandMethod.getAnnotation(Command.class).id();

                // If they match, set the commands method to this one
                if (methodId == commandId) {
                    command.setMethod(commandMethod);
                }
            }
            else if (commandMethod.isAnnotationPresent(SetEntity.class)) {
                // Set the entity of our class object
                commandMethod.invoke(classObject, this);
            }
        }

        // Set the class object for this generic command
        command.setClassObject(classObject);

        // Put the command into the command map
        commandMapId.put(commandId, command);
    }


    public void addCommandClass(String className) {
        // Ensure no duplicates
        for (int i = 0; i < commandClasses.size(); i++) {
            if (className == commandClasses.get(i)) {
                commandClasses.remove(i);
                break;
            }
        }
        // Add the class name
        commandClasses.add(className);
    }

    public List<String> getCommandClasses() { return commandClasses; }
}
