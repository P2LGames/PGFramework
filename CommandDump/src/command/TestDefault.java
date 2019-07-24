package command;

import annotations.SetEntity;
import annotations.Command;
import entity.TestEntity;

public class TestDefault {
    private TestEntity testEntity;

    @Command(commandName = "run", id = 0)
    public void run() {
        int num = 4;
    }

    @Command(commandName = "talk", id = 1)
    public String talk() {
        return "I can talk, yay!!";
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    @SetEntity
    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }
}
