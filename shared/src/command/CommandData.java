package command;

/**
 * Holds the data needed to update a command
 */
public class CommandData {
    String commandName;
    String className;
    String commandSourceCode;

    public CommandData(String commandName, String className, String commandSourceCode) {
        this.commandName = commandName;
        this.className = className;
        this.commandSourceCode = commandSourceCode;
    }



}
