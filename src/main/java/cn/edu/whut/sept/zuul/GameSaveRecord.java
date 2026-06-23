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
    private final int level;
    private final int exp;
    private final int atk;
    private final int def;
    private final int sp;
    private final int gold;
    private final int gameTime;
    private final int dayCount;
    private final int teleportVisits;
    private final boolean victory;
    private final String endingTypeName;
    private final int stepCount;
    private final int itemsCollected;
    private final int quizTotal;
    private final int quizCorrect;
    private final int enemiesDefeated;
    private final int fleeCount;
    private final int roomsVisited;
    private final String npcAffinityData;
    private final String npcTalkedData;
    private final String savedAt;
    private final List<Item> inventoryItems;
    private final List<RoomItemSnapshot> roomItems;
    private final Map<String, String> questProgress;

    public GameSaveRecord(long id, long playerId, String saveName, String currentRoomName,
                          int score, int health, int maxWeight, int currentWeight,
                          int level, int exp, int atk, int def, int sp, int gold,
                          int gameTime, int dayCount, int teleportVisits,
                          boolean victory, String endingTypeName,
                          int stepCount, int itemsCollected, int quizTotal, int quizCorrect,
                          int enemiesDefeated, int fleeCount, int roomsVisited,
                          String npcAffinityData, String npcTalkedData,
                          String savedAt, List<Item> inventoryItems,
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
        this.level = level;
        this.exp = exp;
        this.atk = atk;
        this.def = def;
        this.sp = sp;
        this.gold = gold;
        this.gameTime = gameTime;
        this.dayCount = dayCount;
        this.teleportVisits = teleportVisits;
        this.victory = victory;
        this.endingTypeName = endingTypeName != null ? endingTypeName : "";
        this.stepCount = stepCount;
        this.itemsCollected = itemsCollected;
        this.quizTotal = quizTotal;
        this.quizCorrect = quizCorrect;
        this.enemiesDefeated = enemiesDefeated;
        this.fleeCount = fleeCount;
        this.roomsVisited = roomsVisited;
        this.npcAffinityData = npcAffinityData != null ? npcAffinityData : "";
        this.npcTalkedData = npcTalkedData != null ? npcTalkedData : "";
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

    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getSp() { return sp; }
    public int getGold() { return gold; }
    public int getGameTime() { return gameTime; }
    public int getDayCount() { return dayCount; }
    public int getTeleportVisits() { return teleportVisits; }
    public String getEndingTypeName() { return endingTypeName; }
    public int getStepCount() { return stepCount; }
    public int getItemsCollected() { return itemsCollected; }
    public int getQuizTotal() { return quizTotal; }
    public int getQuizCorrect() { return quizCorrect; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getFleeCount() { return fleeCount; }
    public int getRoomsVisited() { return roomsVisited; }
    public String getNpcAffinityData() { return npcAffinityData; }
    public String getNpcTalkedData() { return npcTalkedData; }

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
