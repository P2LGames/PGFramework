package main.communication.request;

/**
 * The request that holds the data for updating a class
 */
public class UpdateRequest extends Request {
    private String fileContents;
    private Boolean hasParameter;
    private String parameterClassName;

    public String getFileContents() {
        return fileContents;
    }

    public Boolean getHasParameter() {
        return hasParameter;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public void setHasParameter(Boolean hasParameter) {
        this.hasParameter = hasParameter;
    }
}
