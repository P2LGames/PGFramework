package test.command;

import command.CommandResult;
import command.GenericCommand;
import command.StringCommandDefault;
import communication.ServerException;
import main.command.GenericCommandFactory;
import main.command.GenericCommandHandler;
import main.communication.request.CommandRequest;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class CommandHandlerTest {

    @Test
    public void testHandleCommand() {
        try {
            //Setup mock of command factory
            GenericCommandFactory factory = mock(GenericCommandFactory.class);
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setCommand("testCommand");
            commandRequest.setEntityId("testID");
            commandRequest.setParameters(new Object[0]);
            GenericCommand command = new GenericCommand();
            StringCommandDefault stringCommandDefault = new StringCommandDefault();
            command.setClassObject(stringCommandDefault);
            command.setMethod(StringCommandDefault.class.getMethod("getString"));
            command.setParameters(new Object[0]);
            when(factory.getCommand(commandRequest)).thenReturn(command);

            GenericCommandHandler handler = new GenericCommandHandler();
            handler.setCommandFactory(factory);
            CommandResult result = handler.handleCommand(commandRequest);
            CommandResult expectedCommandResult = new CommandResult("I can talk, yay!!", commandRequest.getEntityId());
            expectedCommandResult.setSuccess(true);


            assertEquals(result, expectedCommandResult);
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }
}
