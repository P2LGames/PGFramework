package test.command;

import command.*;
import command.parameter.Input;
import communication.ServerException;
import entity.TestEntity;
import main.command.CommandFactory;
import main.entity.EntityMap;
import main.communication.request.CommandRequest;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CommandFactoryTest {

    @After
    public void tearDown() {
        EntityMap entities = EntityMap.getInstance();
        entities.clear();
    }

    @Test
    public void testTalk() throws NoSuchMethodException, ServerException {
        CommandRequest request = new CommandRequest("testID", "talk", false, null);
        EntityMap entities = EntityMap.getInstance();
        Map<String, Constructor> constructorInstances = new HashMap<>();
        constructorInstances.put("talk", StringCommandDefault.class.getConstructor());
        Map<String, Class> parameterClassNames = new HashMap<>();
        TestEntity entity = new TestEntity(request.getEntityId(), constructorInstances, parameterClassNames);
        entities.put(entity.getEntityID(), entity);
        CommandFactory factory = new CommandFactory();
        Command command = factory.getCommand(request);
        assertEquals(command, new StringCommandDefault());
    }

    @Test
    public void testInputCommand() throws ServerException {
        CommandRequest request = new CommandRequest("testID", "input", true, "{\"string\":\"blah blah blah\", \"integer\":10}");
        EntityMap entities = EntityMap.getInstance();
        TestEntity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        CommandFactory factory = new CommandFactory();
        Command command = factory.getCommand(request);
        assertEquals(command, new InputCommandDefault(new Input("blah blah blah", 10)));
    }

    @Test (expected = ServerException.class)
    public void testBadEntityID() throws ServerException {
        CommandRequest request = new CommandRequest("testID", "talk", false, null);
        CommandFactory factory = new CommandFactory();
        Command command = factory.getCommand(request);
    }

}