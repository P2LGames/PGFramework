package main.communication;

public enum RequestType {
    ENTITY_SETUP(0),
    ENTITY_REGISTER(1),
    ENTITY_UPDATE(2),

    COMMAND(10),
    COMMAND_ERROR(11),

    FILE_GET(20),
    FILE_UPDATE(21),
    UNRECOGNIZED(-1);

    private int numVal;

    RequestType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
