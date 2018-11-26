package main.communication.request;

/**
 * The request that holds the data for updating a class
 */
public class UpdateRequest extends Request {
    private String fileContents;

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }
}
