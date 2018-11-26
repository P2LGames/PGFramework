package test.command;

import command.Command;
import main.command.CommandFactory;
import command.TestTalkDefault;
import main.entity.EntityMap;
import main.communication.request.CommandRequest;
import main.entity.TestEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandFactoryTest {

    @Test
    public void testTalk() {
        CommandRequest request = new CommandRequest("testID", "talk");
        EntityMap entities = EntityMap.getInstance();
        TestEntity entity = new TestEntity(request.getEntityID());
        entities.put(entity.getEntityID(), entity);
        CommandFactory router = new CommandFactory();
        Command command = router.getCommand(request);
        assertEquals(command, new TestTalkDefault());
    }

}