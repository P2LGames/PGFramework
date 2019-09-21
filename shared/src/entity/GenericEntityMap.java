package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to map entity ids to their entity object
 */
public class GenericEntityMap extends ConcurrentHashMap<String, GenericEntity> {
    private static GenericEntityMap instance;

    public GenericEntityMap() {

    }

    public static GenericEntityMap getInstance() {
        if (instance == null) {
            instance = new GenericEntityMap();
        }
        return instance;
    }

    public void removeEntities(ArrayList<String> entityIds) {
        // Loop through all the entity ids
        for (String i: entityIds) {
            // Erase each one
            remove(i);
        }
    }

    public boolean entityExists(String entityId) {
        return containsKey(entityId);
    }
}
