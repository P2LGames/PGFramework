package entity;

import annotations.Entity;
import command.TestDefault;
import communication.ServerException;

/**
 * An example of a concrete implementation of the entity class
 */
@Entity(defaultCommands = TestDefault.class)
public class ProgrammableCube_BP_C extends GenericEntity {

    private int runSpeed;
    private int xPos;
    private int yPos;

    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public ProgrammableCube_BP_C() throws ServerException {
        super();
    }

    public ProgrammableCube_BP_C(String entityID) throws ServerException {
        super(entityID);
    }

    public int getRunSpeed() {
        return runSpeed;
    }

    public void setRunSpeed(int runSpeed) {
        this.runSpeed = runSpeed;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}
