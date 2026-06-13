package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

/**
 * GameState stores the current visible state of the game.
 * It can be used by GUI display, database persistence and tests.
 */
public class GameState {
    private String playerName;
    private String currentRoomName;
    private int score;
    private int health;
    private double currentWeight;
    private double maxWeight;
    private List<String> inventoryItems;
    private List<String> roomItems;

    public GameState() {
        this.playerName = "Player";
        this.currentRoomName = "Unknown";
        this.score = 0;
        this.health = 100;
        this.currentWeight = 0.0;
        this.maxWeight = 10.0;
        this.inventoryItems = new ArrayList<>();
        this.roomItems = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCurrentRoomName() {
        return currentRoomName;
    }

    public void setCurrentRoomName(String currentRoomName) {
        this.currentRoomName = currentRoomName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public List<String> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<String> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public List<String> getRoomItems() {
        return roomItems;
    }

    public void setRoomItems(List<String> roomItems) {
        this.roomItems = roomItems;
    }
}