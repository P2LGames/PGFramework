package test.testutils;

import main.communication.request.EntityRequest;
import main.entity.Entity;
import main.entity.IEntityFactory;
import main.entity.TestEntity;

public class MockEntityFactory implements IEntityFactory {
    @Override
    public Entity getNewEntity(EntityRequest request) {
        return new TestEntity("testEntity0");
    }
}
