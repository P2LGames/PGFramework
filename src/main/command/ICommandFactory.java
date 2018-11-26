package main.command;

import command.Command;
import main.communication.request.CommandRequest;

/**
 * An interface for a ICommandFactory
 */
public interface ICommandFactory {
    Command getCommand(CommandRequest request);
}
