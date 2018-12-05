package main.communication.request;

/**
 * The data needed to load a new command
 */
public class CommandData {
    private String commandName;
    private Boolean hasParameter;
    private String parameterClassName;

    public CommandData(String commandName, Boolean hasParameter, String parameterClassName) {
        this.commandName = commandName;
        this.hasParameter = hasParameter;
        this.parameterClassName = parameterClassName;
    }

    public String getCommandName() {
        return commandName;
    }

    public Boolean getHasParameter() {
        return hasParameter;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }
}
