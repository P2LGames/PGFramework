package command;

/**
 * An example implementation of the Talk command
 */
public class StringCommandDefault extends StringCommand {
    @Override
    public String getString() {
        return "I can talk!!";
    }

}
