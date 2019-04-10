package entity;

import annotations.Entity;
import annotations.Getter;
import annotations.Setter;
import command.PathingEntityDefault;
import communication.ServerException;

import java.util.List;


@Entity(defaultCommands = PathingEntityDefault.class)
public class PathingEntity extends GenericEntity {

    private int xPos;
    private int yPos;
    private List<List<Integer>> map;
    private List<String> orders;

    public PathingEntity() throws ServerException {
        super();
    }

    public PathingEntity(String entityID) throws ServerException {
        super(entityID);
    }

    @Getter(fieldName =  "xPos")
    public int getxPos() {
        return xPos;
    }

    @Setter(fieldName = "xPos", type = Integer.class)
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    @Getter(fieldName =  "yPos")
    public int getyPos() {
        return yPos;
    }

    @Setter(fieldName = "yPos", type = Integer.class)
    public void setyPos(int yPos) {
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
}
