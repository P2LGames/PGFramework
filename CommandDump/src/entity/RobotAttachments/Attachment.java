package entity.RobotAttachments;

import entity.Robot;

public class Attachment {

    Robot robot;
    private Robot.AttachmentPosition attachmentPosition;

    public Attachment(Robot robot, Robot.AttachmentPosition position) {
        this.robot = robot;
        this.attachmentPosition = position;
    }

    public String compileOrder(String order) { return this.getAttachmentPositionString() + "|" + order; }

    public String getAttachmentPositionString() { return this.attachmentPosition.toString(); }

    public Robot.AttachmentPosition getAttachmentPosition() {
        return this.attachmentPosition;
    }

    public void setAttachmentPosition(Robot.AttachmentPosition newPosition) {
        this.attachmentPosition = newPosition;
    }
}
