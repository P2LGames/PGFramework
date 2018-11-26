package main.communication.result;

import java.util.List;
import java.util.Objects;

public class EntityResult {
    private String entityId;
    private List<String> commands;

    public EntityResult(String entityId, List<String> commands) {
        this.entityId = entityId;
        this.commands = commands;
    }

    public String getEntityId() {
        return entityId;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityResult)) return false;
        EntityResult that = (EntityResult) o;
        return Objects.equals(entityId, that.entityId) &&
                Objects.equals(commands, that.commands);
    }
}
