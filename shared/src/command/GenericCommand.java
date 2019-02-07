package command;

import entity.GenericEntity;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A generic command class that defines behavior for a command
 */
public class GenericCommand implements Command, Cloneable {
    private Object classObject;
    private Method method;
    private Object[] _paramValues;
    private GenericEntity entity;

    public GenericCommand() {

    }

    public void setClassObject(Object classObject) {
        this.classObject = classObject;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setParameters(Object[] parameters) {
        _paramValues = parameters;
    }

    public void setEntity(GenericEntity entity) {
        this.entity = entity;
    }

    public GenericEntity getEntity() {
        return entity;
    }

    /**
     * Uses the generic definition of a command and the provided parameter values to run the command
     *
     * @return the result of the command being run
     */
    @Override
    public CommandResult run() {
        CommandResult result = new CommandResult();
        try {
            result.setValue(method.invoke(classObject, _paramValues));
            result.setSuccess(true);
        }
        catch (Exception e) {
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
