package command;

/**
 * The abstract class for a talk command
 */
public abstract class Talker implements Command {
    public abstract String talk();

    @Override
    public Object run() {
        return talk();
    }
}
