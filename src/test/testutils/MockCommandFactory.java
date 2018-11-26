package test.testutils;

import command.Command;
import main.command.ICommandFactory;
import command.TestTalkDefault;
import main.communication.request.CommandRequest;

public class MockCommandFactory implements ICommandFactory {
    @Override
    public Command getCommand(CommandRequest request) {
        return new TestTalkDefault();
    }
}
