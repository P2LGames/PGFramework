package main.communication.request;

import java.util.Map;

public class EntityRequest {
    private String entityType;
    private Map<String, CommandData> commandsMap;

    public EntityRequest(String entityType, Map<String, CommandData> commandsMap) {
        this.entityType = entityType;
        this.commandsMap = commandsMap;
    }

    public String getEntityType() {
        return entityType;
    }

    public Map<String, CommandData> getCommandsMap() {
        return commandsMap;
    }
}
