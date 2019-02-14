package entity;

import java.util.HashMap;

/**
 * A class to map entity ids to their entity object
 */
public class GenericEntityMap extends HashMap<String, GenericEntity> {
    private static GenericEntityMap instance;

    private GenericEntityMap() {
    }

    public static GenericEntityMap getInstance() {
        if(instance == null) {
            instance = new GenericEntityMap();
        }
        return instance;
    }
}
