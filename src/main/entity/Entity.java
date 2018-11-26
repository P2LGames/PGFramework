package main.entity;

import command.Command;
import java.util.Map.Entry;

import java.util.*;

/**
 * Represents an in game entity that is programmable on the client
 */
public abstract class Entity {
    private String entityID;
    private Map<String, Command> defaults;
    private Map<String, Command> commandInstances;

    Entity(String entityID) {
        this.entityID = entityID;
        defaults = new HashMap<>();
        this.commandInstances = new HashMap<>();
    }

    /**
     * A command for getting a command, must be called from a concrete instance of the class
     * @param command
     *  the name of the command
     * @return
     *  the instance that implements the correct command interface
     */
    public abstract Object getCommand(String command);

    /**
     * Replaces the current instance for a class with another(if one already exists)
     *
     * @param commandName
     *  the name of the command
     * @param command
     *  the command that should replace the current one(if one exists)
     */
    public void replaceCommand(String commandName, Command command) {
        commandInstances.put(commandName, command);
    }

    public String getEntityID() {
        return entityID;
    }

    public Map<String, Command> getDefaults() {
        return defaults;
    }

    public Map<String, Command> getCommandInstances() {
        return commandInstances;
    }

    public List<String> getCommandsAsStringList() {
        List<String> commandList = new ArrayList<>();
        Set<Entry<String, Command>> defaultEntries = defaults.entrySet();
        for(Entry entry : defaultEntries) {
            commandList.add((String)entry.getKey());
        }
        return commandList;
    }
}
