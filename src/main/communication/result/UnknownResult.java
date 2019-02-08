package main.communication.result;

import command.Result;

public class UnknownResult extends Result {

    public UnknownResult() {
        this.setErrorMessage("Unknown request type");
        this.setSuccess(false);
    }

}
