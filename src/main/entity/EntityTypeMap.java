package main.entity;

import java.util.concurrent.ConcurrentHashMap;

public class EntityTypeMap extends ConcurrentHashMap<Integer, String> {
    private static EntityTypeMap instance;

    public EntityTypeMap() {}

    public static EntityTypeMap getInstance() {
        if(instance == null) {
            instance = new EntityTypeMap();
        }
        return instance;
    }
}