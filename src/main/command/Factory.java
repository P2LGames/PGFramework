package main.command;

import command.Command;
import main.communication.command.CommandRequest;

/**
 * An interface for a Factory
 */
public interface Factory {
    Command route(CommandRequest request);
}
