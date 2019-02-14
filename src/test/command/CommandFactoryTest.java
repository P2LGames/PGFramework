package test.command;

import command.*;
import communication.ServerException;
import entity.TestEntity;
import main.command.GenericCommandFactory;
import main.communication.request.CommandRequest;
import entity.GenericEntityMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandFactoryTest {

    @After
    public void tearDown() {
        GenericEntityMap entities = GenericEntityMap.getInstance();
        entities.clear();
    }

    @Test
    public void testTalk() {
        try {
            CommandRequest request = new CommandRequest("testID", "talk");
            GenericEntityMap entities = GenericEntityMap.getInstance();
            TestEntity entity = new TestEntity(request.getEntityId());
            entities.put(entity.getEntityID(), entity);
            GenericCommandFactory factory = new GenericCommandFactory();
            Command command = factory.getCommand(request);
            GenericCommand expectedCommand = new GenericCommand();
            expectedCommand.setMethod(TestDefault.class.getMethod("talk"));
            assertEquals(command, expectedCommand);
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }

    @Test (expected = ServerException.class)
    public void testBadEntityID() throws ServerException {
        CommandRequest request = new CommandRequest("testID", "talk");
        GenericCommandFactory factory = new GenericCommandFactory();
        Command command = factory.getCommand(request);
    }

}