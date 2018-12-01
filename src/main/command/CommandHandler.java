package main.command;

import command.Command;
import main.communication.request.CommandRequest;
import command.CommandResult;

import java.lang.reflect.Constructor;

/**
 * This class handles command requests given by the client
 */
public class CommandHandler {
    /**
     * Returns the result of the command returned by the command ICommandFactory
     *
     * @param request
     *  the requests detailing the command to be run
     *
     * @param ICommandFactory
     *  the ICommandFactory that will retrieve the command
     *
     * @return
     *  the result of the command that is run
     */
    public CommandResult handleCommand(CommandRequest request, ICommandFactory ICommandFactory) {
        Command command = ICommandFactory.getCommand(request);
        if(request.getHasParameter()) {
            Class commandClass = command.getClass();
//            Class<?> parameterType = Class.forName();
//            Constructor constructor =
        }
        return command.run();
    }
}
