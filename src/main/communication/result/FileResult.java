package main.communication.result;

import command.Result;

import java.util.Objects;

public class FileResult extends Result {
    private String fileContents;

    public FileResult(Boolean success, String errorMessage) {
        super(errorMessage, success);
    }

    public FileResult(String fileContents) {
        super(null, true);
        this.fileContents = fileContents;
    }

    public String getFileContents() {
        return fileContents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileResult)) return false;
        FileResult that = (FileResult) o;
        return Objects.equals(fileContents, that.fileContents) &&
                Objects.equals(this.getSuccess(), that.getSuccess()) &&
                Objects.equals(this.getErrorMessage(), that.getErrorMessage());
    }
}
