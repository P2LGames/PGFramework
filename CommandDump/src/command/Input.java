package command;

import java.util.Objects;

public class Input {
    private String string;
    private Integer integer;

    public Input(String string, Integer integer) {
        this.string = string;
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Input)) return false;
        Input input = (Input) o;
        return Objects.equals(string, input.string) &&
                Objects.equals(integer, input.integer);
    }
}
