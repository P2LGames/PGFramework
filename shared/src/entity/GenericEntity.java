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
    private Map<Integer, GenericCommand> commandMapId;

    GenericEntity() throws ServerException {
        this.commandMapId = new HashMap<>();
        makeDefaults();
    }

    GenericEntity(String entityID) throws ServerException {
        this.entityID = entityID;
        this.commandMapId = new HashMap<>();
        makeDefaults();
    }

    public String getEntityID() {
        return entityID;
    }

    /**
     * Gets shallow copy of the command with the id provided
     *
     * @param commandId the id of the command to be retrieved
     *
     * @return the generic command object described by the name
     *
     * @throws ServerException thrown when a deep copy of the command is unable to be made
     */
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
     * @param commandId the id of the command to be updated
     * @param command the generic command instance to update it to
     */
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

        // Also look through the parent classes for annotations
        Class<?> clazz = commandsClass;

        // Track the methods we update, don't want to update them twice
        Set<Method> mappedMethods = new HashSet<>();
        boolean setEntity = false;

        // While we aren't looking at the object.class
        while (clazz != Object.class) {
            // Loop through the methods the class contains
            for (Method commandMethod : clazz.getDeclaredMethods()) {
                // If the method has already been mapped, skip it
                if (mappedMethods.contains(commandMethod)) {
                    continue;
                }

                // If an have the command annotation, then set that method up as a command
                if (commandMethod.isAnnotationPresent(Command.class)) {
                    // Add the method to the mapped methods
                    mappedMethods.add(commandMethod);

                    // Get the command id for the maps
                    Integer commandId = commandMethod.getAnnotation(Command.class).id();

                    // Create a generic command
                    GenericCommand command = new GenericCommand();

                    // Set the class object and method of the command
                    command.setClassObject(classObject);
                    command.setMethod(commandMethod);

                    // Add the command to the maps
                    commandMapId.put(commandId, command);
                }
                else if (!setEntity && commandMethod.isAnnotationPresent(SetEntity.class)) {
                    // Set the entity of our class object
                    commandMethod.invoke(classObject, this);

                    // Entity was set
                    setEntity = true;
                }
            }

            // Move to the parent of the current class
            clazz = clazz.getSuperclass();
        }
    }

    public void setupCommandIdWithClass(Class<?> commandsClass, int commandId) throws Exception {
        // Create a class object with the loaded class
        Object classObject = commandsClass.getConstructor().newInstance();

        // Create a generic command
        GenericCommand command = new GenericCommand();

        // Also look through the parent classes for annotations
        Class<?> clazz = commandsClass;

        // Track if we have updated the method or not
        boolean methodMapped = false;
        boolean setEntity = false;

        // While we aren't looking at the object.class
        while (clazz != Object.class) {
            // If we have mapped the method and set the entity, we can stop
            if (methodMapped && setEntity) {
                break;
            }

            // Loop through the methods the class contains
            for (Method commandMethod : clazz.getDeclaredMethods()) {

                // If we have not already mapped the method and if it has the command annotation, see if it matches the command id
                if (!methodMapped && commandMethod.isAnnotationPresent(Command.class)) {

                    // Get the command name and id for the maps
                    int methodId = commandMethod.getAnnotation(Command.class).id();

                    // If they match, set the commands method to this one
                    if (methodId == commandId) {
                        command.setMethod(commandMethod);

                        // We have mapped the method
                        methodMapped = true;
                    }
                }
                // Otherwise, check and see if we can set the entity on the object
                else if (!setEntity && commandMethod.isAnnotationPresent(SetEntity.class)) {
                    // Set the entity of our class object
                    commandMethod.invoke(classObject, this);

                    // Entity mapped
                    setEntity = true;
                }
            }

            // Move to the parent of the current class
            clazz = clazz.getSuperclass();
        }

        // Set the class object for this generic command
        command.setClassObject(classObject);

        // Put the command into the command map
        commandMapId.put(commandId, command);
    }
}
