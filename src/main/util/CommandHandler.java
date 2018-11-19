package main.util;

import command.Command;
import main.command.Router;
import main.communication.command.CommandRequest;
import main.communication.command.CommandResult;

public class CommandHandler {
    public CommandResult handleCommand(CommandRequest request, Router router) {
        CommandResult commandResult = new CommandResult();
        commandResult.setCommand(request.getCommand());
        commandResult.setEntityID(request.getEntityID());
        Command command = router.route(request);
        Object commandOutput = command.run();
        commandResult.setValue(commandOutput);
        return commandResult;
    }
}
