package test.command;

import command.Command;
import main.command.CommandRouter;
import command.TestTalkDefault;
import main.util.EntityMap;
import main.communication.command.CommandRequest;
import main.entity.TestEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandRouterTest {

    @Test
    public void testTalk() {
        CommandRequest request = new CommandRequest("testID", "talk");
        EntityMap entities = EntityMap.getInstance();
        TestEntity entity = new TestEntity(request.getEntityID());
        entities.put(entity.getEntityID(), entity);
        CommandRouter router = new CommandRouter();
        Command command = router.route(request);
        assertEquals(command, new TestTalkDefault());
    }

}