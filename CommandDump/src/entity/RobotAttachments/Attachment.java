package entity.RobotAttachments;

import entity.Robot;

public class Attachment {

    Robot robot;
    private Robot.AttachmentPosition attachmentPosition;

    public Attachment(Robot robot) {
        this.robot = robot;
    }

    public void action() {}

    public Robot.AttachmentPosition getAttachmentPosition() {
        return this.attachmentPosition;
    }

    public void setAttachmentPosition(Robot.AttachmentPosition newPosition) {
        this.attachmentPosition = newPosition;
    }
}
