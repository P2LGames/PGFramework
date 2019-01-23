package main.util;

import entity.Entity;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import main.entity.EntityMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class InMemoryClassLoader {

    public UpdateResult updateClass(UpdateRequest request) {
        UpdateResult result = new UpdateResult();
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

        try {
            Class<?> loadedClass = compiler.compile(request.getCommand(), request.getFileContents());
            if (!result.getSuccess()) {
                return result;
            }
            this.saveSourceCode(request, loadedClass);
            Constructor constructor;
            if (request.getHasParameter()) {
                Class<?> parameterClassObject = Class.forName(request.getParameterClassName());
                constructor = loadedClass.getDeclaredConstructor(parameterClassObject);
            } else {
                constructor = loadedClass.getConstructor();
            }
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityId());
            entity.replaceConstructor(request.getCommand(), constructor);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

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
