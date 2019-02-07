package command;

/**
 * An example implementation of the Talk command
 */
public class StringCommandDefault extends StringCommand {

    @Override
    public String getString() {
        return "I can talk, yay!!";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
