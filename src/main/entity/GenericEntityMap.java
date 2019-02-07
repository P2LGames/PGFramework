package main.entity;

import entity.Entity;
import entity.GenericCommandEntity;

import java.util.HashMap;

public class GenericEntityMap extends HashMap<String, GenericCommandEntity> {
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
