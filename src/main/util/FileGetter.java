package main.util;

import main.communication.request.FileRequest;
import main.communication.request.FileRequestType;
import main.communication.result.FileResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Used for retrieving a file for the user playing the game
 */
public class FileGetter {

    /**
     * Gets the file corresponding to the specified command and returns its contents
     *
     * @param request
     *  the information describing the file to be retrieved
     *
     * @return
     *  the data structure holding the success of the operation and the file contents if it was successful
     */
    public FileResult getFile(FileRequest request) {
        String fileName = request.getCommandName();
        if (fileName.contains(".")) {
            fileName = fileName.replace('.', File.separatorChar);
        } else {
            fileName = "NoPackage" + File.separator + fileName;
        }
        fileName += ".java";
        String fileLocation = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator;
        File file = new File(fileLocation + fileName);
        Scanner scanner;
        try {
            scanner = new Scanner(file).useDelimiter("\\Z");
        } catch (FileNotFoundException e) {
            return new FileResult(false, e.getMessage());
        }
        if (request.getRequestType() == FileRequestType.FILE) {
            return new FileResult(scanner.next());
        } else if (request.getRequestType() == FileRequestType.FUNCTION) {
            String functionCode = this.getFunctionFromFile(scanner.next(), request.getFunctionName());
            return new FileResult(functionCode);
        } else if (request.getRequestType() == FileRequestType.LINE_RANGE) {
            try {
                String lineCode = this.getLinesFromFile(scanner, request.getFirstLine(), request.getLastLine());
                return new FileResult(lineCode);
            } catch (IOException e) {
                return new FileResult(false, e.getMessage());
            }
        }
        return new FileResult(false, "Unknown Request Type: " + request.getRequestType());
    }

    private String getFunctionFromFile(String fileContents, String functionName) {
        return "FIXME: getFunctionFromFile is not defined";
    }

    private String getLinesFromFile(Scanner fileScanner, int firstLine, int lastLine) throws IOException {
        if (firstLine < 0 || lastLine < 0) {
            throw new IOException("Line numbers must be non-negative");
        }
        if (lastLine < firstLine) {
            throw new IOException("Last line number must be greater than or equal to first line number");
        }
        try {
            for (int i = 0; i < firstLine; i++) {
                fileScanner.nextLine();
            }
            String result = "";
            for (int i = firstLine; i <= lastLine; i++) {
                result += fileScanner.nextLine() + '\n';
            }
            return result;
        } catch (Exception e) {
            throw new IOException("Line numbers exceed file length");
        }

    }
}
