package main.communication.request;

public class FileRequest {
    private String commandName;

    public FileRequest(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
