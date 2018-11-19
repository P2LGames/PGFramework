package command;

public abstract class ITalk implements Command {
    public abstract String talk();

    @Override
    public Object run() {
        return talk();
    }
}
