package test.testutils;

import command.Command;
import main.command.Router;
import command.TestTalkDefault;
import main.communication.command.CommandRequest;

public class MockRouter implements Router {
    @Override
    public Command route(CommandRequest request) {
        return new TestTalkDefault();
    }
}
