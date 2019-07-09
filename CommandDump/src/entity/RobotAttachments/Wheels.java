package entity.RobotAttachments;

import entity.Robot;

public class Wheels extends Base {

    public Wheels(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

    @Override
    public void move(Float amount) {
        String strAmount = amount.toString();
        this.robot.addOrder(this.compileOrder(MOVE + strAmount));
    }
    @Override
    public void rotateTo(Float degrees) {
        this.robot.addOrder(this.compileOrder(ROTATE_TO + degrees.toString()));
    }
    @Override
    public void rotateBy(Float degrees) {
        this.robot.addOrder(this.compileOrder(ROTATE_BY + degrees.toString()));
    }
    @Override
    public void rotateLeft() {
        this.robot.addOrder(this.compileOrder(ROTATE_LEFT));
    }
    @Override
    public void rotateRight() {
        this.robot.addOrder(this.compileOrder(ROTATE_RIGHT));
    }

    @Override
    public void stop() {
        this.robot.addOrder(this.compileOrder(STOP));
    }
    @Override
    public void stopRotation() {
        this.robot.addOrder(this.compileOrder(STOP_ROTATION));
    }
    @Override
    public void stopMoving() {
        this.robot.addOrder(this.compileOrder(STOP_MOVEMENT));
    }
}
