package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对应数据库 game_save 表及其背包物品。
 */
public class GameSaveRecord
{
    private final long id;
    private final long playerId;
    private final String saveName;
    private final String currentRoomName;
    private final int score;
    private final int health;
    private final int maxWeight;
    private final int currentWeight;
    private final boolean victory;
    private final String savedAt;
    private final List<Item> inventoryItems;
    private final List<RoomItemSnapshot> roomItems;
    private final Map<String, String> questProgress;

    public GameSaveRecord(long id, long playerId, String saveName, String currentRoomName,
                          int score, int health, int maxWeight, int currentWeight,
                          boolean victory, String savedAt, List<Item> inventoryItems,
                          List<RoomItemSnapshot> roomItems, Map<String, String> questProgress)
    {
        this.id = id;
        this.playerId = playerId;
        this.saveName = saveName;
        this.currentRoomName = currentRoomName;
        this.score = score;
        this.health = health;
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
        this.victory = victory;
        this.savedAt = savedAt;
        this.inventoryItems = inventoryItems != null ? inventoryItems : new ArrayList<>();
        this.roomItems = roomItems != null ? roomItems : new ArrayList<>();
        this.questProgress = questProgress != null ? questProgress : new HashMap<>();
    }

    public long getId()
    {
        return id;
    }

    public long getPlayerId()
    {
        return playerId;
    }

    public String getSaveName()
    {
        return saveName;
    }

    public String getCurrentRoomName()
    {
        return currentRoomName;
    }

    public int getScore()
    {
        return score;
    }

    public int getHealth()
    {
        return health;
    }

    public int getMaxWeight()
    {
        return maxWeight;
    }

    public int getCurrentWeight()
    {
        return currentWeight;
    }

    public boolean isVictory()
    {
        return victory;
    }

    public String getSavedAt()
    {
        return savedAt;
    }

    public List<Item> getInventoryItems()
    {
        return inventoryItems;
    }

    public List<RoomItemSnapshot> getRoomItems()
    {
        return roomItems;
    }

    public Map<String, String> getQuestProgress()
    {
        return questProgress;
    }
}
