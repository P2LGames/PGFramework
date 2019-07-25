package entity.RobotAttachments;

import entity.Robot;

public class Base extends Attachment {

    public enum OrderTypes {
        MOVE(0),
        ROTATE_BY(1),
        ROTATE_TO(2),
        ROTATE_LEFT(3),
        ROTATE_RIGHT(4),
        STOP_ROTATION(5),
        STOP_MOVEMENT(6);

        private int numVal;

        OrderTypes(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    static public final int BASE = 0;

    static public final String MOVE = "MOVE:"; // Needs an amount, positive is forward, negative is backwards
    static public final String ROTATE_TO = "ROTATE_TO:";
    static public final String ROTATE_BY = "ROTATE_BY:";
    static public final String ROTATE_LEFT = ROTATE_TO + "LEFT";
    static public final String ROTATE_RIGHT = ROTATE_TO + "RIGHT";
    static public final String STOP_MOVEMENT = "STOP_MOVEMENT";
    static public final String STOP_ROTATION = "STOP_ROTATION";
    static public final String STOP = "STOP"; // Stops all robot actions, movement, rotation, other

    public Base(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

    public void move(Float amount) {}
    public void stopMoving() {}
    public void stop() {
        this.robot.addOrder(STOP);
    }

    public void rotateTo(Float degrees) {}
    public void rotateBy(Float degrees) {}
    public void rotateLeft() {}
    public void rotateRight() {}
    public void stopRotation() {}

}
