package main.command;

import command.Command;
import command.StringCommand;
import main.entity.EntityMap;
import main.communication.request.CommandRequest;
import main.entity.Entity;

/**
 * This class returns a command based on the request
 */
public class CommandFactory implements ICommandFactory {

    /**
     * This method uses the request to return the correct command
     *
     * @param request
     *  the CommandRequest object to be processed
     *
     * @return
     *  the command that satisfies the request
     */
    @Override
    public Command getCommand(CommandRequest request) {
        //Create the commandResult

        Command command = null;

        //Route the command (will probably be switched to an enum)
        if(request.getCommand().equals("talk")) {
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            command = (StringCommand) entity.getCommand("talk");
        }
        return command;
    }

}
