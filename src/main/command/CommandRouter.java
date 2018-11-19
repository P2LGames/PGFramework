package main.command;

import command.Command;
import command.ITalk;
import main.util.EntityMap;
import main.communication.command.CommandRequest;
import main.entity.Entity;

/**
 * This class routes requests to their commands
 */
public class CommandRouter implements Router {

    /**
     * This method processes a CommandRequest and routes it to the correct command
     *
     * @param request
     *  the CommandRequest object to be processed
     *
     * @return
     *  the result of the command that is ran based off of the request
     */
    @Override
    public Command route(CommandRequest request) {
        //Create the commandResult

        Command command = null;

        //Route the command (will probably be switched to an enum)
        if(request.getCommand().equals("talk")) {
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            command = (ITalk) entity.getCommand("talk");
//            String talkString = talk.talk();
//            commandResult.setValue(talkString);
        }
        return command;
    }

}
