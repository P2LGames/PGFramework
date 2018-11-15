package main.command;

import main.communication.command.CommandRequest;
import main.communication.command.CommandResult;

/**
 * An interface for a Router
 */
public interface Router {
    public CommandResult route(CommandRequest request);
}
