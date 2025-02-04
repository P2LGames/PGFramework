package test.utils;

import entity.GenericEntityMap;
import org.junit.After;

public class MyClassLoaderTest {

    @After
    public void clear() {
        GenericEntityMap entityMap = GenericEntityMap.getInstance();
        entityMap.clear();
    }

//    @Test
//    public void updateClassTest() {
//        try {
//            UpdateRequest request = new UpdateRequest();
//            request.setFileContents("\n" +
//                    "\n" +
//                    "\n" +
//                    "/**\n" +
//                    " * An example implementation of the Talk command\n" +
//                    " */\n" +
//                    "public class Talk {\n" +
//                    "    public String getString() {\n" +
//                    "        return \"I can talk!!\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "\n" +
//                    "}\n");
//            request.setCommand("talk");
//            request.setEntityId("testID1");
//            request.setClassName("Talk");
//            request.setMethodName("getString");
//            request.setParameterTypesStrings(new String[0]);
//
//            GenericEntityMap entities = GenericEntityMap.getInstance();
//            GenericEntity entity = new TestEntity(request.getEntityId());
//            entities.put(entity.getEntityID(), entity);
//
//            InMemoryClassLoader loader = new InMemoryClassLoader();
//
//            UpdateResult result = loader.updateClass(request);
//
//            assertNull(result.getErrorMessage());
//            assertTrue(result.getSuccess());
//        } catch(Exception e) {
//            System.out.println("Unexpected exception: " + e.getMessage());
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void updateClassWithParametersTest() {
//        try {
//            UpdateRequest request = new UpdateRequest();
//            request.setFileContents("" +
//                    "\n" +
//                    "public class ParameterCommand {\n" +
//                    "\n" +
//                    "    public String addString(String inputString) {\n" +
//                    "        return inputString + \"yooooooo\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "}\n");
//            request.setCommand("params");
//            request.setClassName("ParameterCommand");
//            request.setMethodName("addString");
//            request.setEntityId("testId");
//            String[] paramTypes = {"java.lang.String"};
//            request.setParameterTypesStrings(paramTypes);
//
//            GenericEntityMap entities = GenericEntityMap.getInstance();
//            GenericEntity entity = new TestEntity(request.getEntityId());
//            entities.put(entity.getEntityID(), entity);
//            InMemoryClassLoader loader = new InMemoryClassLoader();
//            UpdateResult result = loader.updateClass(request);
//
//            assertNull(result.getErrorMessage());
//            assertTrue(result.getSuccess());
//        } catch(Exception e) {
//            System.out.println("Unexpected exception: " + e.getMessage());
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void updateClassWithParametersReturnTest() {
//        try {
//            Random random = new Random();
//            Integer randInt = random.nextInt();
//
//            UpdateRequest request = new UpdateRequest();
//            request.setFileContents("" +
//                    "\n" +
//                    "public class ParameterCommand {\n" +
//                    "\n" +
//                    "    public String addString(String inputString) {\n" +
//                    "        return inputString + \"yooooooo\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "}\n");
//            request.setCommand("params");
//            request.setClassName("ParameterCommand");
//            request.setMethodName("addString");
//            request.setEntityId("testId");
//            String[] paramTypes = {"java.lang.String"};
//            request.setParameterTypesStrings(paramTypes);
//
//            GenericEntityMap entities = GenericEntityMap.getInstance();
//            GenericEntity entity = new TestEntity(request.getEntityId());
//            entities.put(entity.getEntityID(), entity);
//            InMemoryClassLoader loader = new InMemoryClassLoader();
//            UpdateResult result = loader.updateClass(request);
//
//            assertNull(result.getErrorMessage());
//            assertTrue(result.getSuccess());
//
//            CommandRequest commandRequest = new CommandRequest();
//            Object[] params = {randInt.toString()};
//            commandRequest.setParameters(params);
//            commandRequest.setCommand("params");
//            commandRequest.setEntityId(request.getEntityId());
//
//            CommandHandler handler = new CommandHandler();
//            CommandResult commandResult = handler.handleCommand(commandRequest);
//            CommandResult expectedResult = new CommandResult(randInt + "yooooooo", commandRequest.getEntityId());
//
//            expectedResult.setSuccess(true);
//
//            System.out.println(expectedResult.getValue());
//            System.out.println(commandResult.getValue());
//
//            assertEquals(expectedResult.getValue(), commandResult.getValue());
//        } catch(Exception e) {
//            System.out.println("Unexpected exception: " + e.getMessage());
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void packagedClassTest() {
//        try {
//            UpdateRequest request = new UpdateRequest();
//            request.setFileContents("\n" +
//                    "package test.location;" +
//                    "\n" +
//                    "\n" +
//                    "/**\n" +
//                    " * An example implementation of the Talk command\n" +
//                    " */\n" +
//                    "public class talk {\n" +
//                    "    public String getString() {\n" +
//                    "        return \"I can talk!!\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "\n" +
//                    "}\n");
//            request.setCommand("talk");
//            request.setEntityId("testID1");
//            request.setClassName("test.location.talk");
//            request.setMethodName("getString");
//            request.setParameterTypesStrings(new String[0]);
//
//
//            GenericEntityMap entities = GenericEntityMap.getInstance();
//            GenericEntity entity = new TestEntity(request.getEntityId());
//            entities.put(entity.getEntityID(), entity);
//            InMemoryClassLoader loader = new InMemoryClassLoader();
//
//            UpdateResult result = loader.updateClass(request);
//
//            assertNull(result.getErrorMessage());
//            assertTrue(result.getSuccess());
//
//            try {
//                String expectedFileName = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator
//                        + "test" + File.separator + "location" + File.separator + "talk.java";
//                BufferedReader reader = new BufferedReader(new FileReader(expectedFileName));
//                reader.readLine();
//                assertEquals("package test.location;", reader.readLine());
//            } catch (FileNotFoundException e) {
//                fail("Java file was not created in the expected location");
//            } catch (IOException e) {
//                fail("Could not read Java file");
//            }
//        } catch(Exception e) {
//            System.out.println("Unexpected exception: " + e.getMessage());
//            Assert.fail();
//        }
//    }
//
//
//    @Test
//    public void updateClassReturnTest() {
//        try {
//            GenericEntityMap entities = GenericEntityMap.getInstance();
//            GenericEntity entity = new TestEntity("testID3");
//            entities.put(entity.getEntityID(), entity);
//
//            Random random = new Random();
//            int randInt = random.nextInt();
//
//            // Get the command and the return value
//            CommandRequest commandRequest = new CommandRequest();
//            commandRequest.setCommand("talk");
//            commandRequest.setEntityId(entity.getEntityID());
//            commandRequest.setParameters(new Object[0]);
//
//            // Setup the class loader
//            InMemoryClassLoader loader = new InMemoryClassLoader();
//            // Setup the command handler
//            CommandHandler handler = new CommandHandler();
//
//
//            UpdateRequest request = new UpdateRequest();
//            request.setFileContents("\n" +
//                    "\n" +
//                    "\n" +
//                    "/**\n" +
//                    " * An example implementation of the Talk command\n" +
//                    " */\n" +
//                    "public class Talk {\n" +
//                    "    public String getString() {\n" +
//                    "        return \"I can talk: " + randInt + "\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "\n" +
//                    "}\n");
//            request.setCommand("talk");
//            request.setEntityId(entity.getEntityID());
//            request.setClassName("Talk");
//            request.setMethodName("getString");
//            request.setParameterTypesStrings(new String[0]);
//
//            // Request the update on the entity
//            UpdateResult updateResult = loader.updateClass(request);
//
//            // Check if the values match for the first code
//            CommandResult commandResult = handler.handleCommand(commandRequest);
//            CommandResult expectedCommandResult = new CommandResult("I can talk: " + randInt, commandRequest.getEntityId());
//            expectedCommandResult.setSuccess(true);
//            // ASSERT
//            assertEquals(expectedCommandResult.getValue(), commandResult.getValue());
//
//            // Get another random int
//            randInt = random.nextInt();
//
//            // Change the code
//            request.setFileContents("\n" +
//                    "\n" +
//                    "\n" +
//                    "/**\n" +
//                    " * An example implementation of the Talk command\n" +
//                    " */\n" +
//                    "public class Talk {\n" +
//                    "    public String getString() {\n" +
//                    "        return \"I can talk: " + randInt + "\";\n" +
//                    "    }\n" +
//                    "\n" +
//                    "\n" +
//                    "}\n");
//
//            // Request the update on the entity
//            updateResult = loader.updateClass(request);
//
//            // Check if the values match for the second code
//            commandResult = handler.handleCommand(commandRequest);
//            expectedCommandResult = new CommandResult("I can talk: " + randInt, commandRequest.getEntityId());
//            expectedCommandResult.setSuccess(true);
//            // ASSERT
//            assertEquals(expectedCommandResult.getValue(), commandResult.getValue());
//        } catch(Exception e) {
//            System.out.println("Unexpected exception: " + e.getMessage());
//            Assert.fail();
//        }
//    }
}
