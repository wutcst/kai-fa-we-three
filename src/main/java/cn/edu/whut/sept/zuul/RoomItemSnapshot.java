package cn.edu.whut.sept.zuul;

/**
 * 房间中单个物品的快照数据。
 */
public class RoomItemSnapshot
{
    private final String roomName;
    private final String itemName;
    private final int weight;

    public RoomItemSnapshot(String roomName, String itemName, int weight)
    {
        this.roomName = roomName;
        this.itemName = itemName;
        this.weight = weight;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public String getItemName()
    {
        return itemName;
    }

    public int getWeight()
    {
        return weight;
    }
}
