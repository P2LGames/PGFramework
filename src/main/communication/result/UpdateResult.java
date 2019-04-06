package main.communication.result;

import command.Result;

/**
 * The result from updating a class
 */
public class UpdateResult extends Result {
    public String entityId;

    public UpdateResult(String entityId) {
        super(null, true);
        this.entityId = entityId;
    }
}
