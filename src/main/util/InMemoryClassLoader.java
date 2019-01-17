package main.util;

import entity.Entity;
import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import main.entity.EntityMap;

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
            Constructor constructor;
            if (request.getHasParameter()) {
                Class<?> parameterClassObject = Class.forName(request.getParameterClassName());
                constructor = loadedClass.getDeclaredConstructor(parameterClassObject);
            } else {
                constructor = loadedClass.getConstructor();
            }
            EntityMap entities = EntityMap.getInstance();
            Entity entity = entities.get(request.getEntityID());
            entity.replaceConstructor(request.getCommand(), constructor);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }
}
