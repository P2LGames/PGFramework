package entity.RobotAttachments;

import entity.Robot;

public class Gun extends Arm {

    public enum OrderTypes {
        ATTACK(0),
        ROTATE_PITCH_BY(10),
        ROTATE_PITCH_TO(11);

        private int numVal;

        OrderTypes(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public Gun(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

}
