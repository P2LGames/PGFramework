package test.utils;

import entity.GenericEntity;
import entity.TestEntity;
import main.communication.request.FileRequest;
import main.communication.request.UpdateRequest;
import main.communication.result.FileResult;
import entity.GenericEntityMap;
import main.util.FileGetter;
import main.util.InMemoryClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class FileGetterTest {

    @Before
    public void initialize() {
        try {
            UpdateRequest packageRequest = new UpdateRequest();
            packageRequest.setFileContents(
                    "package test.location;" +
                            "\n" +
                            "public class TestClass{\n" +
                            "    public String getString() {\n" +
                            "        return \"This is a test.\";\n" +
                            "    }\n" +
                            "}");
            packageRequest.setCommand("test.location.TestClass");
            packageRequest.setEntityId("test1");

            UpdateRequest nonPackageRequest = new UpdateRequest();
            nonPackageRequest.setFileContents(
                    "public class TestClass{\n" +
                            "    public String getString() {\n" +
                            "        return \"This is a test.\";\n" +
                            "    }\n" +
                            "}");
            nonPackageRequest.setCommand("TestClass");
            nonPackageRequest.setEntityId("test2");

            UpdateRequest messyRequest = new UpdateRequest();
            messyRequest.setFileContents("import java.util.Map;\n" +
                    "\n" +
                    "public class MessyClass {\n" +
                    "  public Map<String, int[]> GreatFunction1() {\n" +
                    "    // }\n" +
                    "    for (int i = 0; i < 3; i++) {\n" +
                    "      if (i > 3) {\n" +
                    "        System.out.println('}');\n" +
                    "      }\n" +
                    "      /* } */\n" +
                    "    }\n" +
                    "    return Map.of(\n" +
                    "      \"}\", new int[1]\n" +
                    "    );\n" +
                    "  }\n" +
                    "\n" +
                    "  public\n" +
                    "  void    GreatFunction2(int   number,  String\n" +
                    "        word ) {\n" +
                    "    word = \"Great job, you found me!\";\n" +
                    "  }\n" +
                    "}");

            messyRequest.setCommand("MessyClass");
            messyRequest.setEntityId("test3");

            GenericEntityMap entities = GenericEntityMap.getInstance();
            GenericEntity packageEntity = new TestEntity(packageRequest.getEntityId());
            GenericEntity nonPackageEntity = new TestEntity(nonPackageRequest.getEntityId());
            GenericEntity messyEntity = new TestEntity(messyRequest.getEntityId());
            entities.put(packageEntity.getEntityID(), packageEntity);
            entities.put(nonPackageEntity.getEntityID(), nonPackageEntity);
            entities.put(messyEntity.getEntityID(), messyEntity);

            InMemoryClassLoader loader = new InMemoryClassLoader();

            loader.updateClass(packageRequest);
            loader.updateClass(nonPackageRequest);
            loader.updateClass(messyRequest);
        } catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testGetFile() {
        FileRequest request = new FileRequest("TestClass");
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult(
                "public class TestClass{\n" +
                        "    public String getString() {\n" +
                        "        return \"This is a test.\";\n" +
                        "    }\n" +
                        "}");
        assertEquals(result, expectedResult);
    }

    @Test
    public void testGetFileWithPackage() {
        FileRequest request = new FileRequest("test.location.TestClass");
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult(
                "package test.location;" +
                        "\n" +
                        "public class TestClass{\n" +
                        "    public String getString() {\n" +
                        "        return \"This is a test.\";\n" +
                        "    }\n" +
                        "}");
        assertEquals(result, expectedResult);
    }

    @Test
    public void testGetLineNumbers() {
        FileRequest request = new FileRequest("TestClass", 2, 3);
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult(
                "        return \"This is a test.\";\n" +
                        "    }\n");
        assertEquals(result, expectedResult);
    }

    @Test
    public void testInvalidGetLineNumbers() {
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(new FileRequest("TestClass", -1, 0));
        assertFalse(result.getSuccess());
        assertEquals(result.getErrorMessage(), "Line numbers must be non-negative");

        result = fileGetter.getFile(new FileRequest("TestClass", 3, 2));
        assertFalse(result.getSuccess());
        assertEquals(result.getErrorMessage(), "Last line number must be greater than or equal to first line number");

        result = fileGetter.getFile(new FileRequest("TestClass", 1, 8));
        assertFalse(result.getSuccess());
        assertEquals(result.getErrorMessage(), "Line numbers exceed file length");
    }

    @Test
    public void testGetFunction() {
        FileRequest request = new FileRequest("TestClass", "public String getString()");
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult(
                "    public String getString() {\n" +
                "        return \"This is a test.\";\n" +
                "    }");
        assertEquals(result, expectedResult);

        FileRequest request2 = new FileRequest("MessyClass", "public Map<String, int[]> GreatFunction1()");
        FileResult result2 = fileGetter.getFile(request2);
        FileResult expectedResult2 = new FileResult("  public Map<String, int[]> GreatFunction1() {\n" +
                "    // }\n" +
                "    for (int i = 0; i < 3; i++) {\n" +
                "      if (i > 3) {\n" +
                "        System.out.println('}');\n" +
                "      }\n" +
                "      /* } */\n" +
                "    }\n" +
                "    return Map.of(\n" +
                "      \"}\", new int[1]\n" +
                "    );\n" +
                "  }");
        assertEquals(result2, expectedResult2);

        FileRequest request3 = new FileRequest("MessyClass", "public void FakeFunction()");
        FileResult result3 = fileGetter.getFile(request3);
        assertFalse(result3.getSuccess());
        assertEquals(result3.getErrorMessage(), "Function name does not appear in the requested file");

        FileRequest request4 = new FileRequest("MessyClass", "public void GreatFunction2(int   num,  String  )");
        FileResult result4 = fileGetter.getFile(request4);
        FileResult expectedResult4 = new FileResult("  public\n" +
                "  void    GreatFunction2(int   number,  String\n" +
                "        word ) {\n" +
                "    word = \"Great job, you found me!\";\n" +
                "  }");
        assertEquals(result4, expectedResult4);
    }
}