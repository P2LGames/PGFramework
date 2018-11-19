package main.entity;

import command.TestTalkDefault;

/**
 * An example of a concrete implementation of the
 */
public class TestEntity extends Entity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     * @param entityID
     *  the ID of the entity being created
     */
    public TestEntity(String entityID) {
        super(entityID);
        this.getDefaults().put("talk", new TestTalkDefault());
    }

    /**
     * Gets the command that the player defined if they have defined one, otherwise it gets the default
     *
     * @param command
     *  the name of the command
     * @return
     *  the instance of the class corresponding to the class that they want to run
     */
    @Override
    public Object getCommand(String command) {
        if(this.getCommandInstances().containsKey(command)) {
            return this.getCommandInstances().get(command);
        }
        else {
            return this.getDefaults().get(command);
        }
    }
}
