package main.communication.request;

/**
 * The request that holds the data for updating a class
 */
public class UpdateRequest extends Request {
    private String fileContents;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;

    public String getFileContents() {
        return fileContents;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
