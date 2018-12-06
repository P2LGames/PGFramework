package command;

import command.parameter.Input;
import command.returns.Output;

import java.util.Objects;

public class InputCommandDefault extends InputCommand {
    private Input input;

    public InputCommandDefault(Input input) {
        this.input = input;
    }


    @Override
    public Output runOnInput() {
        Output output = new Output();
        output.setInteger(input.getInteger());
        output.setString("it worked");
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputCommandDefault)) return false;
        InputCommandDefault that = (InputCommandDefault) o;
        return Objects.equals(input, that.input);
    }
}
