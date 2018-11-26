package command;

/**
 * The abstract class for a talk command
 */
public abstract class StringCommand implements Command {
    public abstract String getString();

    @Override
    public Object run() {
        return getString();
    }
}
