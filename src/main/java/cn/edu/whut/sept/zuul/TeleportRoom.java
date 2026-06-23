package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 具有传送功能的房间。玩家在此房间尝试移动时，
 * 将被随机传送到游戏中的另一个房间。
 */
public class TeleportRoom extends Room
{
    private final List<Room> allRooms;
    private final Random random;

    /**
     * @param description 房间描述
     * @param allRooms    游戏中所有房间的列表，用于随机选择传送目标
     */
    public TeleportRoom(String description, List<Room> allRooms)
    {
        super(description);
        this.allRooms = new ArrayList<>(allRooms);
        this.random = new Random();
    }

    /**
     * 忽略指定的出口方向，随机返回游戏中的一个房间。
     * 不会将玩家传送到传送房间自身。
     */
    @Override
    public Room getExit(String direction)
    {
        List<Room> candidates = new ArrayList<>();
        for (Room room : allRooms) {
            if (room != this) {
                candidates.add(room);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    /** 所有方向都显示为可用出口（点击任意方向触发随机传送） */
    @Override
    public List<String> getExitDirections()
    {
        List<String> dirs = new ArrayList<>();
        dirs.add("north"); dirs.add("south"); dirs.add("east"); dirs.add("west");
        return dirs;
    }
}
