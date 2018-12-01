package test.command;

import command.*;
import main.command.CommandFactory;
import main.entity.EntityMap;
import main.communication.request.CommandRequest;
import main.entity.TestEntity;
import main.util.Serializer;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CommandFactoryTest {

    @Test
    public void testTalk() throws NoSuchMethodException {
        CommandRequest request = new CommandRequest("testID", "talk", false, null);
        EntityMap entities = EntityMap.getInstance();
        Map<String, Constructor> constructorInstances = new HashMap<>();
        constructorInstances.put("talk", StringCommandDefault.class.getConstructor());
        Map<String, Class> parameterClassNames = new HashMap<>();
        TestEntity entity = new TestEntity(request.getEntityID(), constructorInstances, parameterClassNames);
        entities.put(entity.getEntityID(), entity);
        CommandFactory router = new CommandFactory();
        Command command = router.getCommand(request);
        assertEquals(command, new StringCommandDefault());
    }

    @Test
    public void testInputCommand() throws NoSuchMethodException {
        CommandRequest request = new CommandRequest("testID", "input", true, "{\"string\":\"blah blah blah\", \"integer\":10}");
        EntityMap entities = EntityMap.getInstance();
        Map<String, Constructor> constructorInstances = new HashMap<>();
        constructorInstances.put("input", InputCommandDefault.class.getDeclaredConstructor(Input.class));
        Map<String, Class> parameterClassNames = new HashMap<>();
        parameterClassNames.put("input", Input.class);
        TestEntity entity = new TestEntity(request.getEntityID(), constructorInstances, parameterClassNames);
        entities.put(entity.getEntityID(), entity);
        CommandFactory router = new CommandFactory();
        Command command = router.getCommand(request);
        assertEquals(command, new InputCommandDefault(new Input("blah blah blah", 10)));
    }

}