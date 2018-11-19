package test.testutils;

import command.Command;
import main.command.Factory;
import command.TestTalkDefault;
import main.communication.command.CommandRequest;

public class MockFactory implements Factory {
    @Override
    public Command route(CommandRequest request) {
        return new TestTalkDefault();
    }
}
