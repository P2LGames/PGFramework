package test.command;

import main.command.CommandRouter;
import main.util.EntityMap;
import main.communication.command.CommandRequest;
import main.communication.command.CommandResult;
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
        CommandResult actualCommandResult = router.route(request);
        CommandResult expectedCommandResult = new CommandResult("testID", "talk", "I can talk!!");
        assertEquals(actualCommandResult, expectedCommandResult);
    }

}