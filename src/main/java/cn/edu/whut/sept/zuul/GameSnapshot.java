package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

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
    private boolean victory;
    private List<Item> inventoryItems = new ArrayList<>();

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
}
