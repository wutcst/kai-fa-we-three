package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Map;

/**
 * 从数据库加载后的游戏世界数据。
 */
public class WorldLoadResult
{
    private final Map<String, Room> roomsByDescription;
    private final Room startRoom;

    public WorldLoadResult(Map<String, Room> roomsByDescription, Room startRoom)
    {
        this.roomsByDescription = roomsByDescription;
        this.startRoom = startRoom;
    }

    public Map<String, Room> getRoomsByDescription()
    {
        return roomsByDescription;
    }

    public Room getStartRoom()
    {
        return startRoom;
    }
}
