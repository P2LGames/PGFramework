package main.entity;

import command.Command;
import main.util.Serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * An example of a concrete implementation of the
 */
public class TestEntity extends Entity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     * @param entityID
     *  the ID of the entity being created
     */
    public TestEntity(String entityID) {
        super(entityID);
    }

    public TestEntity(String entityID, Map<String, Constructor> constructorInstances, Map<String, Class> parameterClassNames) {
        super(entityID, constructorInstances, parameterClassNames);
    }

    /**
     * Gets the command that the player defined if they have defined one, otherwise it gets the default
     *
     * @param commandName
     *  the name of the command
     * @return
     *  the instance of the class corresponding to the class that they want to run
     */
    @Override
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

    @Override
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
}
