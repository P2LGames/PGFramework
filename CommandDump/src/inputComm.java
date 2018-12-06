

import command.InputCommand;
import command.parameter.Input;
import command.returns.Output;

public class inputComm extends InputCommand {
    private Input input;

    public inputComm(Input input) {
        this.input = input;
    }


    @Override
    public Output runOnInput() {
        Output output = new Output();
        output.setInteger(input.getInteger());
        output.setString("it worked");
        return output;
    }
}