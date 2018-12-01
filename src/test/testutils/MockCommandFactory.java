package test.testutils;

import command.Command;
import command.StringCommandDefault;
import main.command.ICommandFactory;
import main.communication.request.CommandRequest;

public class MockCommandFactory implements ICommandFactory {
    @Override
    public Command getCommand(CommandRequest request) {
        return new StringCommandDefault();
    }
}
