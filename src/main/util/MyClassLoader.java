package main.util;

import main.communication.request.UpdateRequest;
import main.communication.result.UpdateResult;
import entity.Entity;
import main.entity.EntityMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads classes that do not exist when server is run
 */
public class MyClassLoader extends ClassLoader {

    private UpdateResult result;
    private ClassLoader parent;
    Set<String> loadedClasses = new HashSet<>();
    Set<String> unavaiClasses = new HashSet<>();

    public MyClassLoader() {
        super(MyClassLoader.class.getClassLoader());
        this.parent = MyClassLoader.class.getClassLoader();
        result = new UpdateResult();
    }

    /**
     * Tries to load the class with the provided name with the parent class loader, then loads it with our class loader
     *
     * @param name the name of the class to be loaded
     * @return the class object of the class that was loaded
     */
    @Override
    public Class loadClass(String name) {
        loadedClasses.remove(name);
        EntityMap map = EntityMap.getInstance();
        map.remove(name);
        Class resultClass = null;
        try {
            if (loadedClasses.contains(name) || unavaiClasses.contains(name)) {
                return super.loadClass(name); // Use default CL cache
            }
            else {
                throw new ClassNotFoundException();
            }
        } catch (ClassNotFoundException ex) {
            try {
                // Get the file path
                //String filePath = System.getProperty("user.dir") + File.separator + name + ".class";
                URL url = ClassLoader.getSystemResource(name + ".class");
                if(url == null) {
                    unavaiClasses.add(name);
                    return super.loadClass(name);
                }
                File file = new File(url.toURI());

                // Create a file with the path and get the URL
                //File file = new File(filePath);
                URL myUrl = file.toURI().toURL();

                // Get a connection to the file at the URL
                URLConnection connection = myUrl.openConnection();

                // Create an input stream to the URL
                InputStream input = connection.getInputStream();
                // Create a buffer to write the class data to
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                // While there is still data to be read, read and save it
                int data = input.read();
                while (data != -1) {
                    buffer.write(data);
                    data = input.read();
                }

                // Close our connection
                input.close();

                // Create an array with our bytes
                byte[] classData = buffer.toByteArray();

                // Define our new loaded class by the name, the data, and the length of that data, return that as a class
                resultClass = defineClass(name, classData, 0, classData.length);
                loadedClasses.add(name);
                if (resultClass != null) {
                    if (resultClass.getPackage() == null) {
                        definePackage(name.replaceAll("\\.\\w+$", ""), null, null, null, null, null, null, null);
                    }
                    resolveClass(resultClass);
                }

            } catch (IOException | URISyntaxException | ClassNotFoundException e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return resultClass;
    }

    /**
     * Creates the .class file for the file that we will load
     *
     * @param updateRequest the request holding the data needed to create the class
     * @throws Exception IO and Process exceptions
     */
    private void createClassFile(UpdateRequest updateRequest) throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator + updateRequest.getCommand() + ".java";
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter writer = new PrintWriter(file);
        writer.write(updateRequest.getFileContents());
        writer.close();

        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");

        URL url = ClassLoader.getSystemResource( updateRequest.getCommand() + ".class");
        File classFile = new File(url.toURI());

        ProcessBuilder builder;
        if (isMacOs) {
            builder = new ProcessBuilder("/bin/bash", "-c", "javac " + file.getName()).directory(new File(System.getProperty("user.dir")));
        } else {
            builder = new ProcessBuilder("CMD", "/C", "javac " + file.getName()).directory(new File(System.getProperty("user.dir")));
        }
        Process compilation = builder.start();
        compilation.waitFor();
    }

    /**
     * Updates the class instance held in an Entity object retrieved from the entities list
     *
     * @param request the request holding the data needed to update the clas
     * @return returns the result of updating the class
     */
    public UpdateResult updateClass(UpdateRequest request) {
//        try {
//            createClassFile(request);
//            Class<?> loadedClass = loadClass(request.getCommand());
//            if (!result.getSuccess()) {
//                return result;
//            }
//            Constructor constructor;
//            if (request.getHasParameter()) {
//                Class<?> parameterClassObject = Class.forName(request.getParameterClassName());
//                constructor = loadedClass.getDeclaredConstructor(parameterClassObject);
//            } else {
//                constructor = loadedClass.getConstructor();
//            }
//            EntityMap entities = EntityMap.getInstance();
//            Entity entity = entities.get(request.getEntityID());
//            entity.replaceConstructor(request.getCommand(), constructor);
//            System.out.println("test");
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.setSuccess(false);
//            result.setErrorMessage(e.getMessage());
//        }
        return result;
    }


}
