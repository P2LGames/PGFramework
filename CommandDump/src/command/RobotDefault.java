//// *PERMISSION n,0 *END_PERMISSION
package command;

import annotations.Command;
import annotations.SetEntity;
import entity.GenericEntity;
import entity.Robot;
import entity.RobotAttachments.Base;
import entity.RobotAttachments.Head;
import util.ByteManager;

import java.nio.ByteBuffer;

//// *PERMISSION r,0 *END_PERMISSION

public class RobotDefault {

    //// *PERMISSION w,0 *END_PERMISSION

    int a = 0;
    int w = 0;
    int d = 0;

    /**
     * Called when you have this robot selected, and you press a key.
     * @param code An integer representing the key that you pressed
     * @param pressed Whether or not you pressed or released the key. 1 is pressed, 0 is released.
     */
    public void playerKeyPressed(int code, int pressed) {

        // Press p to see position
        if (code == 80 && pressed == 1) {
            getPosition();
        }
        // Press m too see the map
        else if (code == 77 && pressed == 1) {
            getMapData();
        }
        // Press h to toggle scanning
        else if (code == 72 && pressed == 1) {
            toggleScanning();
        }

        // Movement
        if (code == 87) w = pressed;
        else if (code == 65) a = pressed;
        else if (code == 67) d = pressed;

    }

    public void playerClicked(float x, float y, int pressed) {
        print("Clicked, x: " + x + " y: " + y + " pressed: " + pressed + "\n");
    }

    /**
     * Fill this in to give the robot his orders.
     * Can you help him move around? We gotta get to the finish!
     */
    public void giveOrders() {
        if (w == 1) {
            moveForward();
        }

        if (a == 1) {
            turnLeft();
        }
        else if (d == 1) {
            turnRight();
        }
        else {
            stopTurning();
        }
    }


    /** Tread's interrupt event,
     all actuators will have sometehing like this **/

    public void treadsFinishedOrder() {
        print("Treads finished action\n");
    }


    /** These are the sensor's interrupt events **/

    public void scanned(float[] detectedX, float[] detectedY) {
        for (int i = 0; i < detectedX.length; i++) {
            print("Found at x: " + detectedX[i] + " y: " + detectedY[i] + "\n");
        }
    }

    public void positionRecieved(float x, float y) {
        print("My position is x: " + (int)x + " y: " + (int)y + "\n");
    }

    public void mapReceived(int[][] map) {
        String mapStr = "";

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                mapStr += map[x][y] + " ";
            }
            mapStr += "\n";
        }

        print(mapStr + "\n");
    }

    //// *PERMISSION n,0 *END_PERMISSION

    protected Robot robot = null;

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
                // Handle the data from the click
                // Get the x, y, and the action
                float x = ByteBuffer.wrap(bytes, 8, 4).getFloat();
                float y = ByteBuffer.wrap(bytes, 12, 4).getFloat();
                int action = ByteBuffer.wrap(bytes, 16, 4).getInt();

                // Pass it to the viewable playerKeyPressed function
                this.playerClicked(x, y, action);
            }
        }
        // It might be our base saying we are done moving
        else if (position == Robot.AttachmentPosition.BASE.getNumVal()) {
            // If the type was an action finished
            if (type == Robot.InputType.ACTION_FINISHED.getNumVal()) {
                // Then send the alert
                treadsFinishedOrder();
            }
        }
        // Sensor
        else if (position == Robot.AttachmentPosition.HEAD.getNumVal()) {
            // Event type
            if (type == Robot.InputType.SENSOR_SCAN.getNumVal()) {
                int bodies = ByteBuffer.wrap(bytes, 8, 4).getInt();

                float[] bodiesX = new float[bodies];
                float[] bodiesY = new float[bodies];
                for (int i = 0; i < bodies; i++) {
                    bodiesX[i] = ByteBuffer.wrap(bytes, 12 + i * 4, 4).getFloat();
                    bodiesY[i] = ByteBuffer.wrap(bytes, 16 + i * 4, 4).getFloat();
                }

                scanned(bodiesX, bodiesY);
            }
            else if (type == Robot.InputType.SENSOR_GET_MAP.getNumVal()) {
                int sizeX = ByteBuffer.wrap(bytes, 8, 4).getInt();
                int sizeY = ByteBuffer.wrap(bytes, 12, 4).getInt();

                int[][] map = new int[sizeX][sizeY];

                for (int i = 0; i < sizeX * sizeY; i++) {
                    map[i / sizeX][i % sizeY] = bytes[16 + i];
                            //ByteBuffer.wrap(bytes, 16 + i * 4, 4).getInt();
                }

                mapReceived(map);
            }
            else if (type == Robot.InputType.SENSOR_POSITION.getNumVal()) {
                float x = ByteBuffer.wrap(bytes, 8, 4).getFloat();
                float y = ByteBuffer.wrap(bytes, 12, 4).getFloat();

                this.positionRecieved(x, y);
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

    public void turnRight90() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.ROTATE_BY.getNumVal();

        this.robot.addOrder(position, orderType, ByteManager.convertFloatToByteArray((float)(Math.PI / -2.0)));
    }

    public void turnLeft90() {
        int position = Robot.AttachmentPosition.BASE.getNumVal();
        int orderType = Base.OrderTypes.ROTATE_BY.getNumVal();

        this.robot.addOrder(position, orderType, ByteManager.convertFloatToByteArray((float)(Math.PI / 2.0)));
    }

    public void getMapData() {
        int position = Robot.AttachmentPosition.HEAD.getNumVal();
        int orderType = Head.OrderTypes.GET_MAP.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void getPosition() {
        int position = Robot.AttachmentPosition.HEAD.getNumVal();
        int orderType = Head.OrderTypes.GET_POSITION.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void toggleScanning() {
        int position = Robot.AttachmentPosition.HEAD.getNumVal();
        int orderType = Head.OrderTypes.TOGGLE_POLLING.getNumVal();

        this.robot.addOrder(position, orderType, new byte[0]);
    }

    public void print(String message) {
        robot.printMessage(message);
    }

    public Robot getRobot() { return robot; }

    //// *PERMISSION r,0 *END_PERMISSION

}

