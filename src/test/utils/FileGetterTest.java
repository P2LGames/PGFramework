package test.utils;

import main.communication.request.FileRequest;
import main.communication.result.FileResult;
import main.util.FileGetter;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FileGetterTest {

    @Test
    public void testGetFile() {
        FileRequest request = new FileRequest("talk");
        FileGetter fileGetter = new FileGetter();
        FileResult result = fileGetter.getFile(request);
        FileResult expectedResult = new FileResult("\n" +
                "\n" +
                "import command.StringCommand;\n" +
                "\n" +
                "/**\n" +
                " * An example implementation of the Talk command\n" +
                " */\n" +
                "public class talk extends StringCommand {\n" +
                "    @Override\n" +
                "    public String getString() {\n" +
                "        return \"I can talk!!\";\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}");
        assertEquals(result, expectedResult);
    }
}
