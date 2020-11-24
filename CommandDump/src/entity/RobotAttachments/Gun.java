package entity.RobotAttachments;

import entity.Robot;

public class Gun extends Arm {

    public Gun(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

    public enum OrderTypes {
        SHOOT(0),
        RELOAD(1);

        private int numVal;

        OrderTypes(int numVal) {
            this.numVal = numVal;
        }
        public int getNumVal() {
            return numVal;
        }
    }
}
