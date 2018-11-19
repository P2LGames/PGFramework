package main.command;

import command.Command;
import main.communication.command.CommandRequest;

/**
 * An interface for a Router
 */
public interface Router {
    public Command route(CommandRequest request);
}
