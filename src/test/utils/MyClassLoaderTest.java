package test.utils;
import command.CommandResult;
import communication.ServerException;
import entity.GenericEntity;
import entity.TestEntity;
import main.command.GenericCommandHandler;
import main.communication.request.CommandRequest;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import main.entity.GenericEntityMap;
import main.util.InMemoryClassLoader;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class MyClassLoaderTest {

    @After
    public void clear() {
        GenericEntityMap entityMap = GenericEntityMap.getInstance();
        entityMap.clear();
    }

    @Test
    public void updateClassTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class Talk {\n" +
                "    public String getString() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityId("testID1");
        request.setClassName("Talk");
        request.setMethodName("getString");
        request.setParameterTypes(new Class<?>[0]);

        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);

        InMemoryClassLoader loader = new InMemoryClassLoader();

        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }

    @Test
    public void updateClassWithParametersTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("" +
                "\n" +
                "public class ParameterCommand {\n" +
                "\n" +
                "    public String addString(String inputString) {\n" +
                "        return inputString + \"yooooooo\";\n" +
                "    }\n" +
                "\n" +
                "}\n");
        request.setCommand("params");
        request.setClassName("ParameterCommand");
        request.setMethodName("addString");
        request.setEntityId("testId");
        Class<?>[] paramTypes = {String.class};
        request.setParameterTypes(paramTypes);

        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        InMemoryClassLoader loader = new InMemoryClassLoader();
        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }

    @Test
    public void updateClassWithParametersReturnTest() {
        Random random = new Random();
        Integer randInt = random.nextInt();

        UpdateRequest request = new UpdateRequest();
        request.setFileContents("" +
                "\n" +
                "public class ParameterCommand {\n" +
                "\n" +
                "    public String addString(String inputString) {\n" +
                "        return inputString + \"yooooooo\";\n" +
                "    }\n" +
                "\n" +
                "}\n");
        request.setCommand("params");
        request.setClassName("ParameterCommand");
        request.setMethodName("addString");
        request.setEntityId("testId");
        Class<?>[] paramTypes = {String.class};
        request.setParameterTypes(paramTypes);

        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = new TestEntity(request.getEntityId());
        entities.put(entity.getEntityID(), entity);
        InMemoryClassLoader loader = new InMemoryClassLoader();
        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());

        CommandRequest commandRequest = new CommandRequest();
        Object[] params = {randInt.toString()};
        commandRequest.setParameters(params);
        commandRequest.setCommand("params");
        commandRequest.setEntityId(request.getEntityId());

        GenericCommandHandler handler = new GenericCommandHandler();
        CommandResult commandResult = handler.handleCommand(commandRequest);
        CommandResult expectedResult = new CommandResult(randInt + "yooooooo");

        expectedResult.setSuccess(true);

        System.out.println(expectedResult.getValue());
        System.out.println(commandResult.getValue());

        assertEquals(expectedResult.getValue(), commandResult.getValue());
    }

    @Test
    public void packagedClassTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "package test.location;" +
                "\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk {\n" +
                "    public String getString() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityId("testID1");
        request.setClassName("test.location.talk");
        request.setMethodName("getString");
        request.setParameterTypes(new Class<?>[0]);


        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = new TestEntity(request.getEntityId());
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

        GenericEntityMap entities = GenericEntityMap.getInstance();
        GenericEntity entity = new TestEntity("testID3");
        entities.put(entity.getEntityID(), entity);

        Random random = new Random();
        int randInt = random.nextInt();

        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class Talk {\n" +
                "    public String getString() {\n" +
                "        return \"I can talk: " + randInt + "\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityId(entity.getEntityID());
        request.setClassName("Talk");
        request.setMethodName("getString");
        request.setParameterTypes(new Class<?>[0]);

        InMemoryClassLoader loader = new InMemoryClassLoader();
        UpdateResult updateResult = loader.updateClass(request);
        System.out.println("Error Message: " + updateResult.getErrorMessage());

        // Get the command and the return value
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("talk");
        commandRequest.setEntityId(entity.getEntityID());
        commandRequest.setParameters(new Object[0]);

        GenericCommandHandler handler = new GenericCommandHandler();
        CommandResult commandResult = handler.handleCommand(commandRequest);
        CommandResult expectedCommandResult = new CommandResult("I can talk: " + randInt);
        expectedCommandResult.setSuccess(true);

        System.out.println(expectedCommandResult.getValue());
        System.out.println(commandResult.getValue());

        assertEquals(expectedCommandResult.getValue(), commandResult.getValue());
    }
}
