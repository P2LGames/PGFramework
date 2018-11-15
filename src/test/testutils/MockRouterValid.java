package test.testutils;

import main.command.Router;
import main.communication.command.CommandRequest;
import main.communication.command.CommandResult;

public class MockRouterValid implements Router {

    @Override
    public CommandResult route(CommandRequest request) {
        CommandResult commandResult = new CommandResult();
        if(request.getCommand().equals("testCommand")) {
            commandResult.setValue("I can talk!!");
            commandResult.setCommand("testCommand");
            commandResult.setEntityID(request.getEntityID());
        }
        return commandResult;
    }
}
