package main.entity;

import entity.Entity;

import java.util.HashMap;

public class EntityMap extends HashMap<String, Entity> {
    private static EntityMap instance;

    private EntityMap() {
    }

    public static EntityMap getInstance() {
        if(instance == null) {
            instance = new EntityMap();
        }
        return instance;
    }
}
