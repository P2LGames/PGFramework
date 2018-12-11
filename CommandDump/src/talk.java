

import command.StringCommand;

/**
 * An example implementation of the Talk command
 */
public class talk extends StringCommand {
    @Override
    public String getString() {
        return "I can talk!!";
    }


}
