package main.command;

import command.Command;
import command.Talker;
import main.entity.EntityMap;
import main.communication.command.CommandRequest;
import main.entity.Entity;

/**
 * This class returns a command based on the request
 */
public class CommandFactory implements Factory {

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
    public Command route(CommandRequest request) {
        //Create the commandResult

        Command command = null;

        //Route the command (will probably be switched to an enum)
        if(request.getCommand().equals("talk")) {
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            command = (Talker) entity.getCommand("talk");
        }
        return command;
    }

}
