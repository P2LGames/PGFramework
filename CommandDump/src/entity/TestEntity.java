package entity;

import annotations.Entity;
import annotations.Getter;
import annotations.Setter;
import command.TestDefault;
import communication.ServerException;

/**
 * An example of a concrete implementation of the entity class
 */
@Entity(defaultCommands = TestDefault.class)
public class TestEntity extends GenericEntity {

    private int runSpeed;
    private int xPos;
    private int yPos;

    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public TestEntity() throws ServerException {
        super();
    }

    public TestEntity(String entityID) throws ServerException {
        super(entityID);
    }

    @Getter(fieldName = "runSpeed")
    public int getRunSpeed() {
        return runSpeed;
    }

    @Setter(fieldName = "runSpeed", type = Integer.class)
    public void setRunSpeed(int runSpeed) {
        this.runSpeed = runSpeed;
    }

    @Getter(fieldName = "xPos")
    public int getxPos() {
        return xPos;
    }

    @Setter(fieldName =  "xPos", type = Integer.class)
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    @Getter(fieldName = "yPos")
    public int getyPos() {
        return yPos;
    }

    @Setter(fieldName = "yPos", type = Integer.class)
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}
