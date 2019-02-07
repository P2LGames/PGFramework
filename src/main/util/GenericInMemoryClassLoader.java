package main.util;

import command.GenericCommand;
import entity.GenericEntity;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import main.entity.GenericEntityMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenericInMemoryClassLoader {

    /**
     * Attempts to compile and load a class and returns the result
     *
     * @param request
     *   The request object containing the class name and source code
     * @return
     *   The result object the reports a failure or contains the loaded class
     */
    public UpdateResult updateClass(UpdateRequest request) {
        UpdateResult result = new UpdateResult();
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

        try {
            Class<?> loadedClass = compiler.compile(request.getClassName(), request.getFileContents());
            this.saveSourceCode(request, loadedClass);
            GenericCommand command = new GenericCommand();
            command.setMethod(loadedClass.getMethod(request.getMethodName(), request.getParameterTypes()));
            command.setClassObject(loadedClass.getConstructor().newInstance());
            GenericEntityMap entities = GenericEntityMap.getInstance();
            GenericEntity entity = entities.get(request.getEntityId());
            command.setEntity(entity);
            entity.updateCommand(request.getCommand(), command);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Saves the source code for a compiled class in a .java file for reference
     * @param request
     *    The request object containing the class name and source code
     * @param loadedClass
     *    The compiled class object
     */
    private void saveSourceCode(UpdateRequest request, Class<?> loadedClass) {
        String packageName = loadedClass.getPackageName();
        if (packageName.equals("")) {
            packageName = "NoPackage";
        }
        packageName = packageName.replace('.', File.separatorChar);
        String path = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator + packageName;
        new File(path).mkdirs();
        String fileName = path + File.separator + loadedClass.getSimpleName() + ".java";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write(request.getFileContents());
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
