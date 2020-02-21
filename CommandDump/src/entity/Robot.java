package entity;

import annotations.Entity;
import annotations.Getter;
import annotations.Setter;
import command.RobotDefault;
import communication.ServerException;
import util.ByteManager;

import java.util.ArrayList;

@Entity(defaultCommands = RobotDefault.class)
public class Robot extends GenericEntity {

    private float xPos;
    private float yPos;
    private ArrayList<Byte> orderBytes = new ArrayList<>();

    public Robot() throws ServerException {
        super();
    }

    public Robot(String entityID) throws ServerException {
        super(entityID);
    }

    @Getter(fieldName =  "xPos")
    public float getxPos() {
        return xPos;
    }

    @Setter(fieldName = "xPos", type = Float.class)
    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    @Getter(fieldName =  "yPos")
    public float getyPos() {
        return yPos;
    }

    @Setter(fieldName = "yPos", type = Float.class)
    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public void addOrder(int attachmentPosition, int orderType, byte[] orderParameter) {
        // 3 ints in the front for the position, type, and length, then the order parameter length
        ByteManager.addIntToByteArray(attachmentPosition, this.orderBytes);
        ByteManager.addIntToByteArray(orderParameter.length + 4, this.orderBytes);
        ByteManager.addIntToByteArray(orderType, this.orderBytes);

        // Order parameter bytes
        ByteManager.addBytesToArray(orderParameter, this.orderBytes);
    }

    public byte[] getOrders() {
        return ByteManager.convertArrayListToArray(this.orderBytes);
    }

    public void clearOrders() {
        this.orderBytes.clear();
    }

    public void printMessage(String message) {
        // Integer to represent that the order is going to the robot
        int position = AttachmentPosition.SELF.getNumVal();

        // The order to the robot
        int orderType = OrderTypes.PRINT.getNumVal();

        // Turn the message into bytes
        byte[] messageBytes = message.getBytes();

        // Add an order with all the info
        this.addOrder(position, orderType, messageBytes);
    }


    public enum AttachmentPosition {
        SELF(0), HEAD(1), BASE(2), LEFT(3), RIGHT(4), FRONT(5), BACK(6);

        private int numVal;

        AttachmentPosition(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public enum OrderTypes {
        PRINT(0);

        private int numVal;

        OrderTypes(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public enum AttachmentType {
        SELF(0), ARM(1), GUN(2), SENSOR(3), WHEELS(4), BASE(5);

        private int numVal;

        AttachmentType(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public enum InputType {
        PLAYER_KEY(0),
        PLAYER_MOUSE(1),

        // Sensors can send interrupt events as well
        SENSOR_SCAN(10),
        SENSOR_GET_MAP(11),
        SENSOR_POSITION(12),

        // Actuators can let us know when they finish actions
        ACTION_FINISHED(20);

        private int numVal;

        InputType(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

}
