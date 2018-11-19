package command;

/**
 * An example implementation of the Talk command
 */
public class TestTalkDefault extends Talker {
    @Override
    public String talk() {
        return "I can talk!!";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestTalkDefault;
    }
}
