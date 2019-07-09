package main.communication.request;

/**
 * The request to get the file contents of a file for a command
 */
public class FileRequest {
    private String fileName;
    private FileRequestType type;
    private String functionName;
    private int firstLine;
    private int lastLine;

    public FileRequest(String fileName) {
        this.fileName = fileName;
        this.type = FileRequestType.FILE;
        this.functionName = "";
        this.firstLine = 0;
        this.lastLine = 0;
    }

    public FileRequest(String fileName, String functionName) {
        this.fileName = fileName;
        this.type = FileRequestType.FUNCTION;
        this.functionName = functionName;
        this.firstLine = 0;
        this.lastLine = 0;
    }

    public FileRequest(String fileName, int firstLine, int lastLine) {
        this.fileName = fileName;
        this.type = FileRequestType.LINE_RANGE;
        this.functionName = "";
        this.firstLine = firstLine;
        this.lastLine = lastLine;
    }

    public String getFileName() {
        return fileName;
    }

    public FileRequestType getRequestType() { return type; }

    public String getFunctionName() { return functionName; }

    public int getFirstLine() { return firstLine; }

    public int getLastLine() { return lastLine; }
}
