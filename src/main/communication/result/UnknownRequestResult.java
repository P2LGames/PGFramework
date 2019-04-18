package main.communication.result;

import command.Result;

/**
 * The result if an unknown request is supplied
 */
public class UnknownRequestResult extends Result {

    public UnknownRequestResult() {
        this.setErrorMessage("Unknown request type");
        this.setSuccess(false);
    }

}
