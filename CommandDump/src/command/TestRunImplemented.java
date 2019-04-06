package command;

import entity.TestEntity;

public class TestRunImplemented extends TestDefault {
    @Override
    public void run() {
        //Runs in the positive x direction
        TestEntity entity = getTestEntity();
        entity.setxPos(entity.getxPos() + entity.getRunSpeed());
    }

}
