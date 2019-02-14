package main.util;

import command.CommandData;
import command.GenericCommand;
import entity.GenericEntity;
import main.communication.request.FileRequest;
import main.communication.request.FileRequestType;
import main.communication.result.FileResult;
import main.entity.GenericEntityMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for retrieving a file for the user playing the game
 */
public class FileGetter {

    /**
     * Get data for all the commands associated with a given entity
     * @param entityId - ID of entity you want
     * @return Data for each of the commands associated with a given entity
     */
    public List<CommandData> getCommandsForEntity(String entityId) {
        List<CommandData> allCommandData = new ArrayList<>();
        GenericEntityMap entityMap = GenericEntityMap.getInstance();
        GenericEntity entity = entityMap.get(entityId);
        for (String commandClass : entity.getCommandClasses()) {
            String className;
            GenericCommand command;
            try {
                command = entity.getCommand(commandClass);
                className = command.getClass().getName();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
            FileResult fileResult = this.getFile(new FileRequest(commandClass));
            if (fileResult.getSuccess()) {
                CommandData commandData = new CommandData(commandClass, className, fileResult.getFileContents());
                allCommandData.add(commandData);
            }
        }

        return allCommandData;
    }

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
        } else {
            try {
                if (request.getRequestType() == FileRequestType.FUNCTION) {
                    String functionCode = this.getFunctionFromFile(scanner.next(), request.getFunctionName());
                    return new FileResult(functionCode);
                } else if (request.getRequestType() == FileRequestType.LINE_RANGE) {
                    String lineCode = this.getLinesFromFile(scanner, request.getFirstLine(), request.getLastLine());
                    return new FileResult(lineCode);
                }
            } catch (IOException e ) {
                return new FileResult(false, e.getMessage());
            }
        }
        return new FileResult(false, "Unknown Request Type: " + request.getRequestType());
    }


    /**
     * Extracts a function from a file according to the function's name
     * TODO: This doesn't work with overloaded functions
     * @param fileContents contents of the entire file
     * @param functionName the desired function, of the form: "public returnType functionName (parameters)"
     * @return A string of the function with the same name and return type as the requested function
     * @throws IOException
     */
    private String getFunctionFromFile(String fileContents, String functionName) throws IOException {
        String functionNameRegexString = getRegexForFunctionName(functionName + "{");
        Pattern functionNameRegex = Pattern.compile(functionNameRegexString, Pattern.DOTALL);
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
        patterns.put("\\s*\\(.*?\\)\\s*\\{", "\\\\s*\\\\(.*?\\\\)\\\\s*\\\\{");
        patterns.put("\\s*<\\s*", "\\\\s*<\\\\s*");
        patterns.put("\\s*>", "\\\\s*>");
        patterns.put("\\s*\\[", "\\\\s*\\\\[");
        patterns.put("\\s*]", "\\\\s*]");
        patterns.put("\\s+", "\\\\s+");

        for (String key : patterns.keySet()) {
            Pattern compiledPattern = Pattern.compile(key, Pattern.DOTALL);
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
