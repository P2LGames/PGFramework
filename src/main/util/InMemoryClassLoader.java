package main.util;

import annotations.Command;
import annotations.SetEntity;
import command.GenericCommand;
import entity.GenericEntity;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import entity.GenericEntityMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

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

            // Get the entity via the entity id
            GenericEntity entity = GenericEntityMap.getInstance().get(request.getEntityId());

            // Create a class object with the loaded class
            Object classObject = loadedClass.getConstructor().newInstance();

            // Loop through the methods in the loaded class
            for (Method commandMethod : loadedClass.getDeclaredMethods()) {
                // If we find a SetEntity method, we want to invoke it with the entity found via entityId
                if (commandMethod.isAnnotationPresent(SetEntity.class)) {
                    // Set the entity of our class object
                    commandMethod.invoke(classObject, entity);
                    // Exit the loop
                    break;
                }
            }

            // Set the method and class object for this generic command
            command.setMethod(loadedClass.getMethod(request.getMethodName(), paramTypes));
            command.setClassObject(classObject);

            // Update the command for the entity
            entity.updateCommand(request.getCommand(), command);
            entity.addCommandClass(request.getClassName());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Error: " + e.getLocalizedMessage());
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

        // Get the package
        if (packageName.equals("")) {
            packageName = "NoPackage";
        }

        // Replace periods with slashes
        packageName = packageName.replace('.', File.separatorChar);

        // Get the path to the package folder
        String path = System.getProperty("user.dir") + File.separator + "UserFiles" + File.separator + packageName;

        // Make the directories if they do not exist
        new File(path).mkdirs();

        // Get the file path
        String fileName = path + File.separator + loadedClass.getSimpleName() + ".java";

        System.out.println(request.getFileContents());

        try {
            // Write the file with the file contents
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write(request.getFileContents());
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
