package test.utils;

import entity.Entity;
import entity.TestEntity;
import main.communication.request.FileRequest;
import main.communication.request.UpdateRequest;
import main.communication.result.FileResult;
import main.communication.result.UpdateResult;
import main.entity.EntityMap;
import main.util.FileGetter;
import main.util.InMemoryClassLoader;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FileGetterTest {

    @Before
    public void initialize() {
        UpdateRequest packageRequest = new UpdateRequest();
        packageRequest.setFileContents(//"\n" +
                "package test.location;" +
                "\n" +
                "public class TestClass{\n" +
                "    public String getString() {\n" +
                "        return \"This is a test.\";\n" +
                "    }\n" +
                "}");
        packageRequest.setCommand("test.location.TestClass");
        packageRequest.setEntityId("test1");
        packageRequest.setHasParameter(false);

        UpdateRequest nonPackageRequest = new UpdateRequest();
        nonPackageRequest.setFileContents(//"\n" +
                "public class TestClass{\n" +
                "    public String getString() {\n" +
                "        return \"This is a test.\";\n" +
                "    }\n" +
                "}");
        nonPackageRequest.setCommand("TestClass");
        nonPackageRequest.setEntityId("test2");
        nonPackageRequest.setHasParameter(false);

        EntityMap entities = EntityMap.getInstance();
        Entity packageEntity = new TestEntity(packageRequest.getEntityId());
        Entity nonPackageEntity = new TestEntity(nonPackageRequest.getEntityId());
        entities.put(packageEntity.getEntityID(), packageEntity);
        entities.put(nonPackageEntity.getEntityID(), nonPackageEntity);

        InMemoryClassLoader loader = new InMemoryClassLoader();

        loader.updateClass(packageRequest);
        loader.updateClass(nonPackageRequest);
    }

    @Test
    public void testGetFile() {
        FileRequest request = new FileRequest("TestClass");
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult(//"\n" +
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
        FileResult expectedResult = new FileResult(//"\n" +
                "package test.location;" +
                "\n" +
                "public class TestClass{\n" +
                "    public String getString() {\n" +
                "        return \"This is a test.\";\n" +
                "    }\n" +
                "}");
        assertEquals(result, expectedResult);
    }
}
