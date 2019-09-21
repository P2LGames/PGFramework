package main.entity;

import java.util.HashMap;

public class EntityTypeMap extends HashMap<Integer, String> {
    private static EntityTypeMap instance;

    public EntityTypeMap() {}

    public static EntityTypeMap getInstance() {
        if(instance == null) {
            instance = new EntityTypeMap();
        }
        return instance;
    }
}