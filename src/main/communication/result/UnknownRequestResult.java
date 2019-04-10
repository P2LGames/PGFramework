package main.communication.result;

import command.Result;

public class UnknownRequestResult extends Result {

    public UnknownRequestResult() {
        this.setErrorMessage("Unknown request type");
        this.setSuccess(false);
    }

}
