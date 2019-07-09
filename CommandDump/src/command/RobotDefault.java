//// *READONLY
package command;

import annotations.Command;
import annotations.SetEntity;
import entity.GenericEntity;
import entity.Robot;
import entity.RobotAttachments.Wheels;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotDefault {
    private Robot robot;

    //// *NOACCESS

    static int TYPE_SELF = 1;
    static int TYPE_WHEELS = 2;
    static int TYPE_SENSORS = 3;

    static int POSITION_SELF = 1;
    static int POSITION_BASE = 2;

    @Command(commandName = "setupAttachments")
    public void setupAttachments(String attachments) {
        this.robot.setupAttachments(attachments);
    }

    @Command(commandName = "process")
    public List<String> process() {
        robot.clearOrders();

        giveOrders();

        List<String> orders = robot.getOrders();

        return orders;
    }

    @Command(commandName = "input")
    public String input(byte[] bytes) {
        // Turn the bytes into a stream
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            int position = in.readInt();
            int type = in.readInt();
            String message = in.readUTF();
            // Update our message
            this.message = position + " " + type + " " + message;
        }
        catch (IOException e) {
            return e.getMessage();
        }

        return "";
    }

    @SetEntity
    public void setRobot(GenericEntity robot) {
        this.robot = (Robot)robot;
    }

    //// *READWRITE

    String message = "";

    public void giveOrders() {

        // If the user want's to move, meaning input is -1 or 1
        if (userMove() != 0) {

            // We should move!
            if (userMove() > 0) {
                // Right now I can only move forward, can you fix me?
                moveForward();
            }

        }
        else {
            // Use this if you want to stop the robot's movement
            stopMoving();
        }

        // If the user wants to rotate, meaning input is -1 or 1
        if (userRotate() != 0) {

            // We should rotate
            if (userRotate() < 0) {
                // Right now I can only rotate to the right, can you fix me?
                turnLeft();
            }

        }
        else {
            // Use this if you want tto stop the robot from turning
            stopTurning();
        }

        if (message != "") {
            print(message);
        }

    }

    //// *READONLY

    public int userMove() {
        return robot.getUserInputMove();
    }

    public int userRotate() {
        return robot.getUserInputRotate();
    }

    public void moveForward() {
        robot.getWheels().move(1f);
    }

    public void moveBackward() {
        robot.getWheels().move(-1f);
    }

    public void stopMoving() {
        robot.getWheels().stopMoving();
    }

    public void turnLeft() {
        robot.getWheels().rotateLeft();
    }

    public void turnRight() {
        robot.getWheels().rotateRight();
    }

    public void stopTurning() {
        robot.getWheels().stopRotation();
    }

    public void print(String message) {
        robot.printMessage(message);
    }

    public Robot getRobot() { return robot; }

}