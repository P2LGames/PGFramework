package main.communication.result;

public abstract class Result {
    String errorMessage;
    Boolean success;

    public Result(String errorMessage, Boolean success) {
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
