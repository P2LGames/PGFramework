

import command.InputCommand;
import command.parameter.Input;
import command.returns.Output;
public class inputComm extends command.InputCommand {
    private command.parameter.Input input;

    public inputComm(command.parameter.Input input) {
        this.input = input;
    }


    @Override
    public command.returns.Output runOnInput() {
        command.returns.Output output = new command.returns.Output();
        output.setInteger(input.getInteger());
        output.setString("it worked");
        return output;
    }
}