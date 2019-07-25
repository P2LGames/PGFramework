//// *READONLY
package command;

import annotations.Command;
import annotations.SetEntity;
import entity.GenericEntity;
import entity.Robot;
import entity.RobotAttachments.Base;
import util.ByteManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RobotDefault {
    private Robot robot;

    //// *NOACCESS

    @Command(commandName = "process", id = 0)
    public byte[] process() {
        robot.clearOrders();

        giveOrders();

//        List<String> orders = robot.getOrders();
        byte[] orders = robot.getOrders();
//        System.out.println(orders.length);

        return orders;
    }

    @Command(commandName = "input", id = 1)
    public byte[] input(byte[] bytes) {
        // Turn the bytes into a stream
        int position = ByteBuffer.wrap(bytes, 0, 4).getInt();
        int type = ByteBuffer.wrap(bytes, 4, 4).getInt();
//        System.out.println("Position: " + position + " Type: " + type + " Byte length: " + bytes.length);

        // If it was ourselves, and the player
        if (position == Robot.AttachmentPosition.SELF.getNumVal()
                && type == Robot.InputType.PLAYER.getNumVal()) {

            // There will we two ints, move and rotate that follow
            this.move = ByteBuffer.wrap(bytes, 8, 4).getInt();
            this.rotate = ByteBuffer.wrap(bytes, 12, 4).getInt();

            System.out.println("Move: " + move + " Rotate: " + rotate);
        }

        return new byte[0];
    }

    @SetEntity
    public void setRobot(GenericEntity robot) {
        this.robot = (Robot)robot;
    }

    //// *READWRITE

    private String message = "";
    private int move = 0;
    private int rotate = 0;

    public void giveOrders() {

        // If the user want's to move, meaning input is -1 or 1
        if (this.move != 0) {

            // We should move!
            if (this.move > 0) {
                // Right now I can only move forward, can you fix me?
                moveForward();
            }
            else if (this.move < 0) {
                moveBackward();
            }
        }
        else {
            // Use this if you want to stop the robot's movement
            stopMoving();
        }

        // If the user wants to rotate, meaning input is -1 or 1
        if (this.rotate != 0) {

            // We should rotate
            if (this.rotate > 0) {
                // Right now I can only rotate to the right, can you fix me?
                turnRight();
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

    public void moveForward() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.MOVE.getNumVal();

        this.robot.addOrder(position, orderType, ByteManager.convertFloatToByteArray(1f));
    }

    public void moveBackward() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.MOVE.getNumVal();

        this.robot.addOrder(position, orderType, ByteManager.convertFloatToByteArray(-1f));
    }

    public void stopMoving() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.STOP_MOVEMENT.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void turnLeft() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.ROTATE_LEFT.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void turnRight() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.ROTATE_RIGHT.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void stopTurning() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.STOP_ROTATION.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void print(String message) {
        robot.printMessage(message);
    }

    public Robot getRobot() { return robot; }

}