package test.utils;
import main.util.EntityMap;
import main.communication.command.UpdateRequest;
import main.communication.command.UpdateResult;
import main.entity.Entity;
import main.entity.TestEntity;
import main.util.MyClassLoader;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyClassLoaderTest {

    @Test
    public void updateClassTest() {
        UpdateRequest request = new UpdateRequest();
        request.setFileContents("\n" +
                "\n" +
                "\n" +
                "\n" +
                "public class talk implements ITalk {\n" +
                "    @Override\n" +
                "    public String talk() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityID("testID");

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity(request.getEntityID());
        entities.put(entity.getEntityID(), entity);

        ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
        MyClassLoader loader = new MyClassLoader(parentClassLoader);

        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }
}
