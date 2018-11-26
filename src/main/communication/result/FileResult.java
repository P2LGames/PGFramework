package main.communication.result;

import java.util.Objects;

public class FileResult {
    private String fileContents;
    private Boolean success;
    private String errorMessage;

    public FileResult(Boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public FileResult(String fileContents) {
        this.success = true;
        this.fileContents = fileContents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileResult)) return false;
        FileResult that = (FileResult) o;
        return Objects.equals(fileContents, that.fileContents) &&
                Objects.equals(success, that.success) &&
                Objects.equals(errorMessage, that.errorMessage);
    }
}
