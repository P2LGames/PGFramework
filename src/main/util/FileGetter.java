package main.util;

import main.communication.request.FileRequest;
import main.communication.result.FileResult;

import java.io.File;
import java.io.FileNotFoundException;
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
        String fileName = System.getProperty("user.dir") + File.separator + request.getCommandName() + ".java";
        File file = new File(fileName);
        Scanner scanner;
        try {
            scanner = new Scanner(file).useDelimiter("\\Z");
        } catch (FileNotFoundException e) {
            return new FileResult(false, e.getMessage());
        }
        return new FileResult(scanner.next());
    }

}
