package main.util;

import command.GenericCommand;
import entity.GenericEntity;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import entity.GenericEntityMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InMemoryClassLoader {

    /**
     * Attempts to compile and load a class and returns the result
     *
     * @param request
     *   The request object containing the class name and source code
     * @return
     *   The result object the reports a failure or contains the loaded class
     */
    public UpdateResult updateClass(UpdateRequest request) {
        UpdateResult result = new UpdateResult(request.getEntityId());
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

        try {
            Class<?> loadedClass = compiler.compile(request.getClassName(), request.getFileContents());
            this.saveSourceCode(request, loadedClass);
            GenericCommand command = new GenericCommand();
            String[] paramTypesStrings = request.getParameterTypesStrings();
            Class<?>[] paramTypes = new Class<?>[paramTypesStrings.length];
            for(int i = 0; i < paramTypesStrings.length; i++) {
                paramTypes[i] = Class.forName(paramTypesStrings[i]);
            }
            command.setMethod(loadedClass.getMethod(request.getMethodName(), paramTypes));
            command.setClassObject(loadedClass.getConstructor().newInstance());
            GenericEntityMap entities = GenericEntityMap.getInstance();
            GenericEntity entity = entities.get(request.getEntityId());
            command.setEntity(entity);
            entity.updateCommand(request.getCommand(), command);
            entity.addCommandClass(request.getClassName());
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
