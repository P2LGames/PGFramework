package command;

import entity.GenericEntity;
import entity.GenericEntityMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A generic command class that defines behavior for a command
 */
public class GenericCommand implements Command, Cloneable {
    private Object classObject;
    private Method method;
    private Object[] _paramValues;

    public GenericCommand() {
        _paramValues = new Object[0];
    }

    public GenericCommand(Object classObject, Method method, Object[] _paramValues) {
        this.classObject = classObject;
        this.method = method;
        this._paramValues = _paramValues;
    }

    public void setClassObject(Object classObject) {
        this.classObject = classObject;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setParameters(Object[] parameters) {
        this._paramValues = parameters;
    }

    /**
     * Uses the generic definition of a command and the provided parameter values to run the command
     *
     * @return the result of the command being run
     */
    @Override
    public CommandResult run() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        assert(parameterTypes.length == _paramValues.length);

        // If one of the parameters is the generic entity class, then one of the _paramValues should be an entity ID
        for (int i = 0; i < parameterTypes.length; i++) {

            Class classType = parameterTypes[i];
            if (classType.isInstance(GenericEntity.class)) {
                assert(_paramValues[i] instanceof String);
                String id = (String) _paramValues[i];
                GenericEntityMap entityMap = GenericEntityMap.getInstance();
                GenericEntity entity = entityMap.get(id);
                _paramValues[i] = entity;
            }
        }

        CommandResult result = new CommandResult();
        try {
//            // If the method has parameters
//            if (parameterTypes.length > 0) {
//                // Then run the method with our passes parameters
//
//            }
//            // Otherwise, run the method with not parameters
//            else {
//                result.setValue(method.invoke(classObject));
//            }
            result.setValue(method.invoke(classObject, _paramValues));
            result.setSuccess(true);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            System.out.println("Cause: " + cause.getMessage());
            result.setErrorMessage(cause.getMessage());
            result.setSuccess(false);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
            result.setErrorMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    public GenericCommand clone() throws CloneNotSupportedException {
        return (GenericCommand) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericCommand)) return false;
        GenericCommand that = (GenericCommand) o;
        return Objects.equals(method, that.method);
    }
}
