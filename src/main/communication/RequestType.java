package main.communication;

public enum RequestType {
    ENTITY_SETUP(0),
    ENTITY_REGISTER(1),
    COMMAND(2),
    ENTITY_UPDATE(3),
    FILE_GET(4),
    FILE_UPDATE(5),
    UNRECOGNIZED(-1);

    private int numVal;

    RequestType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
