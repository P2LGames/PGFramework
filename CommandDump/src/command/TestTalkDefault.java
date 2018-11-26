package command;

/**
 * An example implementation of the Talk command
 */
public class TestTalkDefault extends StringCommand {
    @Override
    public String getString() {
        return "I can talk!!";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestTalkDefault;
    }
}
