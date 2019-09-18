package entity.RobotAttachments;

import entity.Robot;

public class Head extends Attachment {

    public enum OrderTypes {
        SCAN(0),
        GET_MAP(1),
        GET_POSITION(2),

        TOGGLE_POLLING(10);

        private int numVal;

        OrderTypes(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public Head(Robot robot, Robot.AttachmentPosition position) {
        super(robot, position);
    }

}
