package entity.RobotAttachments;

import entity.Robot;

public class Base extends Attachment {

    static public final String MOVE = "MOVE:"; // Needs an amount, positive is forward, negative is backwards
    static public final String ROTATE_TO = "ROTATE_TO:";
    static public final String ROTATE_BY = "ROTATE_BY:";
    static public final String STOP_MOVEMENT = "STOP_MOVEMENT";
    static public final String STOP_ROTATION = "STOP_ROTATION";
    static public final String STOP = "STOP"; // Stops all robot actions, movement, rotation, other

    public Base(Robot robot) {
        super(robot);
    }

    public void move(Float amount) {}
    public void stopMoving() {}
    public void stop() {
        this.robot.addOrder(STOP);
    }

    public void rotateTo(Float degrees) {}
    public void rotateBy(Float degrees) {}
    public void stopRotation() {}

}
