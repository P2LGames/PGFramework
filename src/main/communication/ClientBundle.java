package main.communication;

import java.util.Objects;

/**
 * The class that will be passed over from the client
 */
public class ClientBundle {
    private RequestType type;
    private String serializedData;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientBundle)) return false;
        ClientBundle that = (ClientBundle) o;
        return type == that.type &&
                Objects.equals(serializedData, that.serializedData);
    }
}
