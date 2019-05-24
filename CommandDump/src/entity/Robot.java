package entity;

import annotations.Entity;
import annotations.Getter;
import annotations.Setter;
import command.PathingEntityDefault;
import command.RobotDefault;
import communication.ServerException;
import entity.RobotAttachments.*;
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
        this.addAttachment(new Wheels(this), AttachmentType.WHEELS);
    }

    public Robot(String entityID) throws ServerException {
        super(entityID);
        this.addAttachment(new Wheels(this), AttachmentType.WHEELS);
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

    @Getter(fieldName =  "orders")
    public List<String> getOrders() {
        return orders;
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

    public void clearOrders() {
        this.orders.clear();
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

        return null;
    }

    public Sensor getSensor() {
        if (typeToAttachments.containsKey(AttachmentType.SENSOR)) {
            return (Sensor) typeToAttachments.get(AttachmentType.SENSOR).get(0);
        }
//        if (head instanceof Sensor) {
//            return (Sensor)head;
//        }
        return null;
    }

    public void printMessage(String message) {
        orders.add("PRINT:" + message);
    }

    public class AttachmentInfo {
        public AttachmentType type;
        public String attachmentJson;

        public AttachmentInfo() {}
    }

    public enum AttachmentPosition {
        HEAD, BASE, ARM_ONE, ARM_TWO, FRONT, BACK
    }

    public enum AttachmentType {
        ARM, GUN, SENSOR, WHEELS
    }



}
