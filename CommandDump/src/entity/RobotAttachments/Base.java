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

    public Base(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

    public void move(Float amount) {}
    public void stopMoving() {}
    public void stop() {}

    public void rotateTo(Float degrees) {}
    public void rotateBy(Float degrees) {}
    public void rotateLeft() {}
    public void rotateRight() {}
    public void stopRotation() {}

}
