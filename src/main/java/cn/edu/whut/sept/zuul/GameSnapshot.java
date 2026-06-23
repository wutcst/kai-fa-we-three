package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 某一时刻的游戏状态快照，用于存档读写。
 */
public class GameSnapshot
{
    private String currentRoomName;
    private int score;
    private int health;
    private int maxWeight;
    private int currentWeight;
    private int level = 1;
    private int exp = 0;
    private int atk = 10;
    private int def = 5;
    private int sp = 50;
    private int gold = 0;
    private int gameTime = 480;
    private int dayCount = 1;
    private int teleportVisits = 0;
    private boolean victory;
    private String endingTypeName = "";
    private int stepCount = 0;
    private int itemsCollected = 0;
    private int quizTotal = 0;
    private int quizCorrect = 0;
    private int enemiesDefeated = 0;
    private int fleeCount = 0;
    private int roomsVisited = 0;
    private String npcAffinityData = "";
    private String npcTalkedData = "";
    private List<Item> inventoryItems = new ArrayList<>();
    private List<RoomItemSnapshot> roomItems = new ArrayList<>();
    private Map<String, String> questProgress = new HashMap<>();

    public String getCurrentRoomName()
    {
        return currentRoomName;
    }

    public void setCurrentRoomName(String currentRoomName)
    {
        this.currentRoomName = currentRoomName;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getHealth()
    {
        return health;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public int getMaxWeight()
    {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight)
    {
        this.maxWeight = maxWeight;
    }

    public int getCurrentWeight()
    {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight)
    {
        this.currentWeight = currentWeight;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }

    public int getDef() { return def; }
    public void setDef(int def) { this.def = def; }

    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getGameTime() { return gameTime; }
    public void setGameTime(int gameTime) { this.gameTime = gameTime; }
    public int getDayCount() { return dayCount; }
    public void setDayCount(int dayCount) { this.dayCount = dayCount; }
    public int getTeleportVisits() { return teleportVisits; }
    public void setTeleportVisits(int teleportVisits) { this.teleportVisits = teleportVisits; }
    public String getEndingTypeName() { return endingTypeName; }
    public void setEndingTypeName(String endingTypeName) { this.endingTypeName = endingTypeName; }
    public int getStepCount() { return stepCount; }
    public void setStepCount(int stepCount) { this.stepCount = stepCount; }
    public int getItemsCollected() { return itemsCollected; }
    public void setItemsCollected(int itemsCollected) { this.itemsCollected = itemsCollected; }
    public int getQuizTotal() { return quizTotal; }
    public void setQuizTotal(int quizTotal) { this.quizTotal = quizTotal; }
    public int getQuizCorrect() { return quizCorrect; }
    public void setQuizCorrect(int quizCorrect) { this.quizCorrect = quizCorrect; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public void setEnemiesDefeated(int enemiesDefeated) { this.enemiesDefeated = enemiesDefeated; }
    public int getFleeCount() { return fleeCount; }
    public void setFleeCount(int fleeCount) { this.fleeCount = fleeCount; }
    public int getRoomsVisited() { return roomsVisited; }
    public void setRoomsVisited(int roomsVisited) { this.roomsVisited = roomsVisited; }
    public String getNpcAffinityData() { return npcAffinityData; }
    public void setNpcAffinityData(String npcAffinityData) { this.npcAffinityData = npcAffinityData; }
    public String getNpcTalkedData() { return npcTalkedData; }
    public void setNpcTalkedData(String npcTalkedData) { this.npcTalkedData = npcTalkedData; }

    public boolean isVictory()
    {
        return victory;
    }

    public void setVictory(boolean victory)
    {
        this.victory = victory;
    }

    public List<Item> getInventoryItems()
    {
        return inventoryItems;
    }

    public void setInventoryItems(List<Item> inventoryItems)
    {
        this.inventoryItems = inventoryItems;
    }

    public List<RoomItemSnapshot> getRoomItems()
    {
        return roomItems;
    }

    public void setRoomItems(List<RoomItemSnapshot> roomItems)
    {
        this.roomItems = roomItems;
    }

    public Map<String, String> getQuestProgress()
    {
        return questProgress;
    }

    public void setQuestProgress(Map<String, String> questProgress)
    {
        this.questProgress = questProgress;
    }
}
