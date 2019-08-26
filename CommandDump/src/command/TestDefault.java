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
    public byte[] talk() {
        return "I can talk, yay!!".getBytes();
    }

    @Command(commandName = "timeout", id = 2)
    public void timeout() {
        try {
            for (int i = 0; i < 10000; i++) {
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {}
    }

    @Command(commandName = "first", id = 3)
    public byte[] first() {
        try {
            Thread.sleep(300);
        }
        catch (InterruptedException e) {}

        return "I ran first".getBytes();
    }

    @Command(commandName = "second", id = 4)
    public byte[] second() {
        return "I ran second".getBytes();
    }

    @Command(commandName = "asyncLast", id = 5)
    public byte[] asyncLast() {
        try {
            Thread.sleep(300);
        }
        catch (InterruptedException e) {}

        return "I ran last".getBytes();
    }

    @Command(commandName = "asyncFirst", id = 6)
    public byte[] asyncFirst() {
        return "I ran first".getBytes();
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    @SetEntity
    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }
}
