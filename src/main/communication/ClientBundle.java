package main.communication;

/**
 * The class that will be passed over from the client
 */
public class ClientBundle {
    private RequestType type;
    private String serializedRequest;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getSerializedRequest() {
        return serializedRequest;
    }

    public void setSerializedRequest(String serializedRequest) {
        this.serializedRequest = serializedRequest;
    }
}
