package command;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class GenericCommand implements Command, Cloneable {
    private Object classObject;
    private Method method;
    private Object[] _paramValues;

    public GenericCommand() {

    }

    public void setClassObject(Object classObject) {
        this.classObject = classObject;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void set_paramValues(Object[] _paramValues) {
        this._paramValues = _paramValues;
    }

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

    public void setParameters(Object[] parameters) {
        _paramValues = parameters;
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
