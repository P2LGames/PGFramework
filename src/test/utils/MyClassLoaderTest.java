package test.utils;
import command.CommandResult;
import communication.ServerException;
import entity.TestEntity;
import main.command.CommandHandler;
import main.communication.request.CommandRequest;
import main.entity.EntityMap;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import entity.Entity;
import main.util.InMemoryClassLoader;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class MyClassLoaderTest {

    @After
    public void clear() {
        EntityMap entityMap = EntityMap.getInstance();
        entityMap.clear();
    }

    @Test
    public void updateClassTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "import command.StringCommand;\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk extends command.StringCommand {\n" +
                "    @Override\n" +
                "    public String getString() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityId("testID1");
        request.setHasParameter(false);

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);

        InMemoryClassLoader loader = new InMemoryClassLoader();

        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }

    @Test
    public void updateClassTestWithParameters() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "import command.InputCommand;\n" +
                "import command.parameter.Input;\n" +
                "import command.returns.Output;\n" +
                "public class inputComm extends command.InputCommand {\n" +
                "    private command.parameter.Input input;\n" +
                "\n" +
                "    public inputComm(command.parameter.Input input) {\n" +
                "        this.input = input;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    @Override\n" +
                "    public command.returns.Output runOnInput() {\n" +
                "        command.returns.Output output = new command.returns.Output();\n" +
                "        output.setInteger(input.getInteger());\n" +
                "        output.setString(\"it worked\");\n" +
                "        return output;\n" +
                "    }\n" +
                "}");
        request.setCommand("inputComm");
        request.setEntityId("testID2");
        request.setHasParameter(true);
        request.setParameterClassName("command.parameter.Input");

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        InMemoryClassLoader loader = new InMemoryClassLoader();

        UpdateResult result = loader.updateClass(request);


        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }

    @Test
    public void packagedClassTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "package test.location;" +
                "\n" +
                "import command.StringCommand;\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk extends command.StringCommand {\n" +
                "    @Override\n" +
                "    public String getString() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("test.location.talk");
        request.setEntityId("testID1");
        request.setHasParameter(false);

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        InMemoryClassLoader loader = new InMemoryClassLoader();

        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());

        try {
            String expectedFileName = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator
                    + "test" + File.separator + "location" + File.separator + "talk.java";
            BufferedReader reader = new BufferedReader(new FileReader(expectedFileName));
            reader.readLine();
            assertEquals("package test.location;", reader.readLine());
        } catch (FileNotFoundException e) {
            fail("Java file was not created in the expected location");
        } catch (IOException e) {
            fail("Could not read Java file");
        }
    }


    @Test
    public void updateClassReturnTest() throws ServerException {

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity("testID3");
        entities.put(entity.getEntityID(), entity);

        Random random = new Random();
        int randInt = random.nextInt();

        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "import command.StringCommand;\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk extends command.StringCommand {\n" +
                "    @Override\n" +
                "    public String getString() {\n" +
                "        return \"I can talk: " + randInt + "\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityId(entity.getEntityID());
        request.setHasParameter(false);

//        MyClassLoader loader = new MyClassLoader();
        InMemoryClassLoader loader = new InMemoryClassLoader();
        UpdateResult updateResult = loader.updateClass(request);
        System.out.println("Error Message: " + updateResult.getErrorMessage());

        // Get the command and the return value
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("talk");
        commandRequest.setEntityId(entity.getEntityID());
        commandRequest.setHasParameter(false);

        CommandHandler handler = new CommandHandler();
//        handler.setCommandFactory(factory);
        CommandResult commandResult = handler.handleCommand(commandRequest);
        CommandResult expectedCommandResult = new CommandResult("I can talk: " + randInt);

        System.out.println(expectedCommandResult.getValue());
        System.out.println(commandResult.getValue());

        assertEquals(expectedCommandResult.getValue(), commandResult.getValue());

//        request = new UpdateRequest();
//        request.setFileContents("\n" +
//                "\n" +
//                "import command.StringCommand;\n" +
//                "\n" +
//                "/**\n" +
//                " * An example implementation of the Talk command\n" +
//                " */\n" +
//                "public class talk extends command.StringCommand {\n" +
//                "    @Override\n" +
//                "    public String getString() {\n" +
//                "        return \"I can talk too!!\";\n" +
//                "    }\n" +
//                "\n" +
//                "\n" +
//                "}\n");
//        request.setCommand("talk");
//        request.setEntityId("testID");
//        request.setHasParameter(false);
//
//        updateResult = loader.updateClass(request);
//
//        // Get the command and the return value
//        commandResult = handler.handleCommand(commandRequest);
//        expectedCommandResult = new CommandResult("I can talk too!!");
//
//        assertEquals(expectedCommandResult.getValue(), commandResult.getValue());
    }
}
