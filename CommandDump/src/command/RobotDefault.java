package command;

import annotations.Command;
import annotations.SetEntity;
import entity.GenericEntity;
import entity.Robot;
import entity.RobotAttachments.Wheels;

import java.util.ArrayList;
import java.util.List;

public class RobotDefault {
    private Robot robot;

    @Command(commandName = "setupAttachments")
    public void setupAttachments(String attachments) {
        this.robot.setupAttachments(attachments);
    }

    @Command(commandName = "process")
    public List<String> process() {
        robot.clearOrders();

        giveOrders();

        return robot.getOrders();
    }

    public void giveOrders() {

        if (robot.getUserInputRotate() != 0) {
            // Stop moving if we want to rotate
            this.robot.getWheels().stopMoving();

            if (robot.getUserInputRotate() < 0) {
                this.robot.getWheels().rotateBy(-1f);
            }
            else {
                this.robot.getWheels().rotateBy(1f);
            }
        }
        else if (robot.getUserInputMove() != 0) {
            // Stop rotation if we want to move
            this.robot.getWheels().stopRotation();

            if (robot.getUserInputMove() < 0) {
                this.robot.getWheels().move(-1f);
            }
            else {
                this.robot.getWheels().move(1f);
            }
        }
        else {
            this.robot.getWheels().stopRotation();
            this.robot.getWheels().stopMoving();
        }

        this.robot.printMessage("Hello!");
    }

    public Robot getRobot() { return robot; }

    @SetEntity
    public void setRobot(GenericEntity robot) {
        this.robot = (Robot)robot;
    }
}
