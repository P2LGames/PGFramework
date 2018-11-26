package main.util;

import main.communication.request.FileRequest;
import main.communication.result.FileResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileGetter {

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
