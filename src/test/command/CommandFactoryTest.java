package test.command;

import command.*;
import command.parameter.Input;
import communication.ServerException;
import entity.TestEntity;
import main.command.GenericCommandFactory;
import main.communication.request.CommandRequest;
import main.entity.GenericEntityMap;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CommandFactoryTest {

    @After
    public void tearDown() {
        GenericEntityMap entities = GenericEntityMap.getInstance();
        entities.clear();
    }

    @Test
    public void testTalk() throws NoSuchMethodException, ServerException {
        CommandRequest request = new CommandRequest("testID", "talk");
        GenericEntityMap entities = GenericEntityMap.getInstance();
        TestEntity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        GenericCommandFactory factory = new GenericCommandFactory();
        Command command = factory.getCommand(request);
        GenericCommand expectedCommand = new GenericCommand();
        expectedCommand.setMethod(StringCommandDefault.class.getMethod("getString"));
        assertEquals(command, expectedCommand);
    }

//    @Test
//    public void testInputCommand() throws ServerException {
//        CommandRequest request = new CommandRequest("testID", "input", true, "{\"string\":\"blah blah blah\", \"integer\":10}");
//        GenericEntityMap entities = GenericEntityMap.getInstance();
//        TestEntity entity = new TestEntity(request.getEntityId());
//        entities.put(entity.getEntityID(), entity);
//        GenericCommandFactory factory = new GenericCommandFactory();
//        Command command = factory.getCommand(request);
//        assertEquals(command, new InputCommandDefault(new Input("blah blah blah", 10)));
//    }

    @Test (expected = ServerException.class)
    public void testBadEntityID() throws ServerException {
        CommandRequest request = new CommandRequest("testID", "talk");
        GenericCommandFactory factory = new GenericCommandFactory();
        Command command = factory.getCommand(request);
    }

}