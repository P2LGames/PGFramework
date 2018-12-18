package test.command;

import command.CommandResult;
import command.StringCommandDefault;
import main.command.CommandException;
import main.command.CommandFactory;
import main.command.CommandHandler;
import main.communication.request.CommandRequest;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class CommandHandlerTest {

    @Test
    public void testHandleCommand() throws CommandException {
        //Setup mock of command factory
        CommandFactory factory = mock(CommandFactory.class);
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("testCommand");
        commandRequest.setEntityID("testID");
        commandRequest.setHasParameter(false);
        when(factory.getCommand(commandRequest)).thenReturn(new StringCommandDefault());

        CommandHandler handler = new CommandHandler();
        handler.setCommandFactory(factory);
        CommandResult result = handler.handleCommand(commandRequest);
        CommandResult expectedCommandResult = new CommandResult("I can talk!!");


        assertEquals(result, expectedCommandResult);
    }
}
