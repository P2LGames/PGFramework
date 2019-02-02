package main.util;

import main.communication.request.FileRequest;
import main.communication.request.FileRequestType;
import main.communication.result.FileResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            try {
                String functionCode = this.getFunctionFromFile(scanner.next(), request.getFunctionName());
                return new FileResult(functionCode);
            } catch (IOException e) {
                return new FileResult(false, e.getMessage());
            }
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

    private String getFunctionFromFile(String fileContents, String functionName) throws IOException {
        /*
            TODO:
            This function needs some work. It behaves correctly in most instances,
            but it is missing some functionality and a few safeguards.
            -It isn't very robust for functions with parameters
            -If the function name appears in the source code in a comment or string
            before the actual function, this will find the wrong thing.
         */
        String functionNameRegexString = getRegexForFunctionName(functionName + "{");
        Pattern functionNameRegex = Pattern.compile(functionNameRegexString);
        Matcher matcher = functionNameRegex.matcher(fileContents);

        if (matcher.find()) {
            StringBuilder functionContents = new StringBuilder();
            functionContents.append(matcher.group(0));
            int end = matcher.end();
            int braceBalance = 1;
            boolean inString = false;
            boolean inChar = false;
            boolean inComment = false;
            boolean inBlockComment = false;
            for (int i = end; i < fileContents.length() && braceBalance > 0; i++) {
                char c = fileContents.charAt(i);
                functionContents.append(c);
                if (inString && c == '"' && fileContents.charAt(i-1) != '\\') {
                    inString = false;
                } else if (inChar && c == '\'' && fileContents.charAt(i-1) != '\\') {
                    inChar = false;
                } else if (inComment && c == '\n') {
                    inComment = false;
                } else if (inBlockComment && c == '/' && fileContents.charAt(i-1) == '*') {
                    inBlockComment = false;
                } else if (inString || inChar || inComment || inBlockComment) {
                    continue;
                } else if (c == '"') {
                    inString = true;
                } else if (c == '\'') {
                    inChar = true;
                } else if (c == '/' && fileContents.charAt(i-1) == '/') {
                    inComment = true;
                } else if (c == '*' && fileContents.charAt(i-1) == '/') {
                    inBlockComment = true;
                } else if (c == '{') {
                    braceBalance++;
                } else if (c == '}') {
                    braceBalance--;
                }
            }
            return functionContents.toString();
        }
        else {
            throw new IOException("Function name does not appear in the requested file");
        }
    }

    private String getRegexForFunctionName(String functionName) {
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("\\)\\s*\\{", "\\)\\\\s*\\\\{");
        patterns.put("\\(\\s*\\)","(\\\\s*\\\\)");
        patterns.put("\\s*\\(", "\\\\s*\\\\(");
        patterns.put(" \\)", "\\\\)");
        patterns.put("\\s*<\\s*", "\\\\s*<\\\\s*");
        patterns.put("\\s*>", "\\\\s*>");
        patterns.put("\\s*\\[", "\\\\s*\\\\[");
        patterns.put("\\s*]", "\\\\s*]");
        patterns.put("\\s+", "\\\\s+");

        for (String key : patterns.keySet()) {
            Pattern compiledPattern = Pattern.compile(key);
            functionName = compiledPattern.matcher(functionName).replaceAll(patterns.get(key));
        }
        return "[\\t ]*" + functionName;
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
