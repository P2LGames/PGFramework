package main.entity;

import main.communication.request.EntityRequest;

public interface IEntityFactory {
    Entity getNewEntity(EntityRequest request);
}
