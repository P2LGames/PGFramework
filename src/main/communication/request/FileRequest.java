package main.communication.request;

/**
 * The request to get the file contents of a file for a command
 */
public class FileRequest {
    private String commandName;

    public FileRequest(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
