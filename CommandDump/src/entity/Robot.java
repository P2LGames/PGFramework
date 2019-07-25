package entity;

import annotations.Entity;
import annotations.Getter;
import annotations.Setter;
import command.PathingEntityDefault;
import command.RobotDefault;
import communication.ServerException;
import entity.RobotAttachments.*;
import util.ByteManager;
import util.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity(defaultCommands = RobotDefault.class)
public class Robot extends GenericEntity {

    private float xPos;
    private float yPos;
    private List<List<Integer>> map;
    private ArrayList<String> orders = new ArrayList<>();
    private ArrayList<Byte> orderBytes = new ArrayList<>();
    private ArrayList<Attachment> attachments = new ArrayList<>();
    // A map from attachment type to the attachments of that type
    private HashMap<AttachmentType, ArrayList<Attachment>> typeToAttachments = new HashMap<>();

    private Attachment base = null;
    private Attachment armOne = null;
    private Attachment armTwo = null;
    private Attachment head = null;
    private Attachment front = null;
    private Attachment back = null;

    // Movement variables
    private int userInputRotate = 0;
    private int userInputMove = 0;

    public Robot() throws ServerException {
        super();
        this.addAttachment(new Wheels(this, AttachmentPosition.BASE), AttachmentType.WHEELS);
    }

    public Robot(String entityID) throws ServerException {
        super(entityID);
        this.addAttachment(new Wheels(this, AttachmentPosition.BASE), AttachmentType.WHEELS);
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

//    @Getter(fieldName =  "orders")
//    public List<String> getOrders() {
//        return orders;
//    }
    public byte[] getOrders() {
        return ByteManager.convertArrayListToArray(this.orderBytes);
    }

    @Getter(fieldName = "map")
    public List<List<Integer>> getMap() {
        return map;
    }

    @Setter(fieldName =  "map", type = List.class)
    public void setMap(List<List<Integer>> map) {
        this.map = map;
    }

    @Getter(fieldName = "userInputMove")
    public int getUserInputMove() { return this.userInputMove; }

    @Setter(fieldName = "userInputMove", type = Double.class)
    public void setUserInputMove(double userInputMove) { this.userInputMove = (int)userInputMove; }

    @Getter(fieldName = "userInputRotate")
    public int getUserInputRotate() { return this.userInputRotate; }

    @Setter(fieldName = "userInputRotate", type = Double.class)
    public void setUserInputRotate(double userInputRotate) { this.userInputRotate = (int)userInputRotate; }

    public void addOrder(String order) {
        this.orders.add(order);
    }

    public void addOrder(int attachmentPosition, int orderType, byte[] orderParameter) {
        // 3 ints in the front for the position, type, and length, then the order parameter length
        ByteManager.addIntToByteArray(attachmentPosition, this.orderBytes);
        ByteManager.addIntToByteArray(orderType, this.orderBytes);
        ByteManager.addIntToByteArray(orderParameter.length, this.orderBytes);

        // Order parameter bytes
        ByteManager.addBytesToArray(orderParameter, this.orderBytes);
    }

//    public void clearOrders() {
//        this.orders.clear();
//    }
    public void clearOrders() {
        this.orderBytes.clear();
    }

    /**
     * Sets up each attachment that this Robot has. A special format must be used an passed to this function from the Client, or it will
     * not function properly. The format is described below.
     * @param attachments A string of attachments of this format =>
     *                    "[ {"type":ATTACHMENT_TYPE,
     *                        "attachmentInfo": {"position":HEAD(BOTTOM, ARM_ONE...), "attr2":"Hello", etc} },
     *                    {}, ... ]"
     */
    public void setupAttachments(String attachments) {
        // Deserialize our list of attachments to loop through them
        String[] attachmentList = Serializer.deserialize(attachments, String[].class);

        // Loop through each attachment, we will deserialize it into an info object for our attachment
        for (String attachmentInfo: attachmentList) {
            // Get the info as an object
            AttachmentInfo info = Serializer.deserialize(attachmentInfo, AttachmentInfo.class);

            // Use that info to create the attachment with all of its info as the specified type
            Attachment attachment = null;
            switch (info.type) {
                case ARM:
                    attachment = Serializer.deserialize(info.attachmentJson, Arm.class);
                case GUN:
                    attachment = Serializer.deserialize(info.attachmentJson, Gun.class);
                case WHEELS:
                    attachment = Serializer.deserialize(info.attachmentJson, Wheels.class);
                case SENSOR:
                    attachment = Serializer.deserialize(info.attachmentJson, Sensor.class);
                default:
                    attachment = Serializer.deserialize(info.attachmentJson, Arm.class);
            }

            // Proceed to go ham with said attachment
            addAttachment(attachment, info.type);
        }
    }

    public void addAttachment(Attachment attachment, AttachmentType type) {
        // If the attachments for this type is empty, we need to make a new ArrayList for it
        if (!typeToAttachments.containsKey(type)) {
            typeToAttachments.put(type, new ArrayList<>());
        }

        // Add the attachment to a map using its type for easy getting
        typeToAttachments.get(type).add(attachment);
    }




    public Wheels getWheels() {
        if (typeToAttachments.containsKey(AttachmentType.WHEELS)) {
            return (Wheels)typeToAttachments.get(AttachmentType.WHEELS).get(0);
        }
//        if (base instanceof Wheels) {
//            return (Wheels)base;
//        }
        this.printMessage("NO WHEELS");

        return null;
    }

    public Gun getGun() {
        if (typeToAttachments.containsKey(AttachmentType.GUN)) {
            return (Gun)typeToAttachments.get(AttachmentType.GUN).get(0);
        }
//        if (armOne instanceof Gun) {
//            return (Gun)armOne;
//        }
//        else if (armTwo instanceof Gun) {
//            return (Gun)armTwo;
//        }
        this.printMessage("NO GUN");

        return null;
    }

    public Sensor getSensor() {
        if (typeToAttachments.containsKey(AttachmentType.SENSOR)) {
            return (Sensor) typeToAttachments.get(AttachmentType.SENSOR).get(0);
        }
//        if (head instanceof Sensor) {
//            return (Sensor)head;
//        }
        this.printMessage("NO SENSOR");

        return null;
    }

//    public void printMessage(String message) {
//        orders.add("SELF|PRINT:" + message);
//    }
    public void printMessage(String message) {
        // Integer to represent that the order is going to the robot
        ByteManager.addIntToByteArray(AttachmentPosition.SELF.getNumVal(), this.orderBytes);

        // The order to the robot
        ByteManager.addIntToByteArray(OrderTypes.PRINT.getNumVal(), this.orderBytes);

        // Turn the message into bytes
        byte[] messageBytes = message.getBytes();

        // Length of the message
        ByteManager.addIntToByteArray(messageBytes.length, this.orderBytes);

        // Add the message to the array
        ByteManager.addBytesToArray(messageBytes, this.orderBytes);
    }

    public class AttachmentInfo {
        public AttachmentType type;
        public String attachmentJson;

        public AttachmentInfo() {}
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
        PLAYER(0);

        private int numVal;

        InputType(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

}
