package main.command;

public enum CommandError {
    TIMEOUT(0);

    private int numVal;

    CommandError(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
