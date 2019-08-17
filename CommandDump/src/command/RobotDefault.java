//// *NOACCESS
package command;

import annotations.Command;
import annotations.SetEntity;
import entity.GenericEntity;
import entity.Robot;
import entity.RobotAttachments.Base;
import util.ByteManager;

import java.nio.ByteBuffer;

//// *READONLY

public class RobotDefault {

    /**
     * Definition: Function
     *
     * A FUNCTION is a method or routine that you call to run or execute a part of your code.
     * Below is the function W_Pressed.
     * It is called whenever you press W while you have the robot selected.
     */

    /**
     * This function is called whenever you press W.
     * Get the robot to move forward when you press W!
     */
    //// *PERMISSION w,1:r,0 *END_PERMISSION
    public void W_Pressed() {
        /**
         * Whenever you put '//' in front of something, you comment it out. This means that the computer
         * will ignore it when it runs your code.
         *
         * The '// moveForward();' code below is commented out and will not run when you press W.
         * Try removing the comments and recompiling.
         */

        // moveForward();
    }

    //// *PERMISSION n,0 *END_PERMISSION

    /**
     * Called when you have this robot selected, and you press a key.
     * @param code An integer representing the key that you pressed
     * @param pressed Whether or not you pressed or released the key. 1 is pressed, 0 is released.
     */
    public void playerKeyPressed(int code, int pressed) {

        if (code == 87 && pressed == 1) {
            W_Pressed();
        }

    }

    /**
     * Fill this in to give the robot his orders.
     * Can you help him move around? We gotta get to the finish!
     */
    public void giveOrders() {

    }

    /**
     * These are the functions you can call on your robot!
     * Use them well.
     */

    // moveForward()
    // moveBackward()
    // stopMoving()
    // turnLeft()
    // turnRight()
    // stopTurning()
    // print(String)



    private Robot robot;

    public boolean isActionPressed(int pressed) {
        return pressed == 1;
    }

    @Command(commandName = "process", id = 0)
    public byte[] process() {
        giveOrders();

        byte[] orders = robot.getOrders();

        // Clear the robot's orders for the next loop
        robot.clearOrders();

        return orders;
    }

    @Command(commandName = "input", id = 1)
    public byte[] input(byte[] bytes) {
        // Turn the bytes into a stream
        int position = ByteBuffer.wrap(bytes, 0, 4).getInt();
        int type = ByteBuffer.wrap(bytes, 4, 4).getInt();
//        System.out.println("Position: " + position + " Type: " + type + " Byte length: " + bytes.length);

        // If the signal came from the robot
        if (position == Robot.AttachmentType.SELF.getNumVal()) {

            // If the input type was a player key stroke
            if (type == Robot.InputType.PLAYER_KEY.getNumVal()) {
                // Handle the data from the keystroke
                // Get the character code and the action
                int code = ByteBuffer.wrap(bytes, 8, 4).getInt();
                int action = ByteBuffer.wrap(bytes, 12, 4).getInt();

                // Pass it to the viewable playerKeyPressed function
                this.playerKeyPressed(code, action);
            }
            // If the input type was a player mouse input
            else if (type == Robot.InputType.PLAYER_MOUSE.getNumVal()) {

            }

        }

        return new byte[0];
    }

    @SetEntity
    public void setRobot(GenericEntity robot) {
        this.robot = (Robot)robot;
    }

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

    //// *READONLY

}


