package command;

import annotations.Command;
import annotations.SetEntity;
import communication.ServerException;
import entity.PathingEntity;
import util.Serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PathingEntityDefault {
    private PathingEntity pathingEntity;

    @Command(commandName = "run", id = 0)
    public List<String> run(int x, int y) {
        return buildOrders(x, y);
    }

    @Command(commandName = "setMap", id = 1)
    public void setMap(List<List<Integer>> map) {
        pathingEntity.setMap(map);
    }

    public List<String> buildOrders(int x, int y) {
        return buildOrdersRecurse(pathingEntity.getxPos(), pathingEntity.getyPos(), x, y, new ArrayList<>(), pathingEntity.getMap());
    }

    public List<String> buildOrdersRecurse(int startX, int startY, int desX, int desY, List<String> path, List<List<Integer>> map) {
        if(startX == desX && startY == desY) {
            return path;
        }

        map.get(startY).set(startX, 1);
        List<String> rPath = null;
        if(startX + 1 < map.get(0).size()) {
            if(map.get(startY).get(startX + 1) != 1) {
                List<String> temp = new ArrayList<>(path);
                temp.add("right");
                rPath = buildOrdersRecurse(startX + 1, startY, desX, desY, temp, cloneMap(map));
            }
        }

        List<String> lPath = null;
        if(startX - 1 >= 0) {
            if(map.get(startY).get(startX - 1) != 1) {
                List<String> temp = new ArrayList<>(path);
                temp.add("left");
                lPath = buildOrdersRecurse(startX - 1, startY, desX, desY, temp, cloneMap(map));
            }
        }

        List<String> dPath = null;
        if(startY + 1 < map.size()) {
            if(map.get(startY + 1).get(startX) != 1) {
                List<String> temp = new ArrayList<>(path);
                temp.add("down");
                dPath = buildOrdersRecurse(startX, startY + 1, desX, desY, temp, cloneMap(map));
            }
        }

        List<String> uPath = null;
        if(startY - 1 >= 0) {
            if(map.get(startY - 1).get(startX) != 1) {
                List<String> temp = new ArrayList<>(path);
                temp.add("up");
                uPath = buildOrdersRecurse(startX, startY - 1, desX, desY, temp, cloneMap(map));
            }
        }

        List<List<String>> paths = new ArrayList<>();
        if(rPath != null) {
            paths.add(rPath);
        }
        if(lPath != null) {
            paths.add(lPath);
        }
        if(dPath != null) {
            paths.add(dPath);
        }
        if(uPath != null) {
            paths.add(uPath);
        }

        if (paths.stream().mapToInt(List::size).min().isPresent()) {
            paths.stream().mapToInt(List::size).min().getAsInt();
            return paths.stream().min(Comparator.comparing(List::size)).get();
        }

        return null;

    }

    public List<List<Integer>> cloneMap(List<List<Integer>> map) {
        List<List<Integer>> mapClone = new ArrayList<>();
        for(int i = 0; i < map.size(); i++) {
            List<Integer> row = map.get(i);
            mapClone.add(new ArrayList<>(row));
        }
        return mapClone;
    }

    @SetEntity
    public void setPathingEntity(PathingEntity pathingEntity) {
        this.pathingEntity = pathingEntity;
    }

    public static void main(String[] args) throws ServerException {
        List<List<Integer>> map = new ArrayList<>();
        map.add(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        map.add(Arrays.asList(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1));
        map.add(Arrays.asList(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1));
        map.add(Arrays.asList(1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1));
        map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        map.add(Arrays.asList(1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1));
        map.add(Arrays.asList(1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1));
        map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1));
        map.add(Arrays.asList(1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1));
        map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        map.add(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

        PathingEntity pathingEntity = new PathingEntity();
        pathingEntity.setxPos(1);
        pathingEntity.setyPos(1);
        pathingEntity.setMap(map);

        PathingEntityDefault pathingEntityDefault = new PathingEntityDefault();
        pathingEntityDefault.setPathingEntity(pathingEntity);
        //System.out.println(map.get(6).get(3));
        System.out.println(pathingEntityDefault.buildOrders(3, 6));
    }
}
