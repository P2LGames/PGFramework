package entity.RobotAttachments;

import entity.Robot;

public class Wheels extends Base {

    public float speed = 1.0f;

    public Wheels(Robot robot) {
        super(robot);
    }

    @Override
    public void move(Float amount) {
        String strAmount = amount.toString();

        this.robot.addOrder(MOVE + strAmount);
    }
    @Override
    public void rotateTo(Float degrees) {
        this.robot.addOrder(ROTATE_TO + degrees.toString());
    }
    @Override
    public void rotateBy(Float degrees) {
        this.robot.addOrder(ROTATE_BY + degrees.toString());
    }

    @Override
    public void stop() {
        this.robot.addOrder(STOP);
    }
    @Override
    public void stopRotation() {
        this.robot.addOrder(STOP_ROTATION);
    }
    @Override
    public void stopMoving() { this.robot.addOrder(STOP_MOVEMENT); }
}
