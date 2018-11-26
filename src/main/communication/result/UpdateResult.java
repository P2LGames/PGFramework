package main.communication.result;

/**
 * The result from updating a class
 */
public class UpdateResult {
    private Boolean success;
    private String errorMessage;

    public UpdateResult() {
        this.success = true;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
