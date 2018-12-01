package test.utils;
import main.entity.EntityMap;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
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
                "import command.StringCommand;\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk extends StringCommand {\n" +
                "    @Override\n" +
                "    public String talk() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n");
        request.setCommand("talk");
        request.setEntityID("testID");
        request.setHasParameter(false);

        EntityMap entities = EntityMap.getInstance();
        Entity entity = new TestEntity(request.getEntityID());
        entities.put(entity.getEntityID(), entity);

        MyClassLoader loader = new MyClassLoader();

        UpdateResult result = loader.updateClass(request);

        assertNull(result.getErrorMessage());
        assertTrue(result.getSuccess());
    }
}
