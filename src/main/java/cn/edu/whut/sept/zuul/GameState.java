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
    private int maxHealth = 10;
    private double currentWeight;
    private double maxWeight;
    private String roomDescription;
    private List<String> inventoryItems;
    private List<String> roomItems;
    private List<String> availableExits;
    private List<String> npcs;
    private int level;
    private int exp;
    private int expToNext;
    private int atk;
    private int def;
    private int sp;
    private int gold;
    private int teleportVisits;
    private boolean inCombat;
    private String enemyName;
    private int enemyHp;
    private int enemyMaxHp;
    private int enemyAtk;
    private int enemyDef;
    private int enemyLevel;
    private List<String> questList = new ArrayList<>();
    private String timeDisplay = "08:00";
    private String timeOfDay = "早晨";
    private int dayCount = 1;
    private String endingTitle = "";
    private String endingDesc = "";
    private int finalScore = 0;
    private int stepCount = 0;
    private int itemsCollected = 0;
    private int enemiesDefeated = 0;
    private boolean victory;
    private String questSummary;
    private boolean loggedIn;

    public GameState() {
        this.playerName = "Player";
        this.currentRoomName = "Unknown";
        this.score = 0;
        this.health = 100;
        this.currentWeight = 0.0;
        this.maxWeight = 10.0;
        this.roomDescription = "Welcome to the World of Zuul.";
        this.inventoryItems = new ArrayList<>();
        this.roomItems = new ArrayList<>();
        this.availableExits = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.level = 1;
        this.exp = 0;
        this.expToNext = 100;
        this.atk = 10;
        this.def = 5;
        this.sp = 50;
        this.gold = 0;
        this.victory = false;
        this.questSummary = "主线=started，支线(cookie)=not_started";
        this.loggedIn = false;
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

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

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

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
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

    public List<String> getAvailableExits() {
        return availableExits;
    }

    public void setAvailableExits(List<String> availableExits) {
        this.availableExits = availableExits;
    }

    public List<String> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<String> npcs) {
        this.npcs = npcs;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getExpToNext() { return expToNext; }
    public void setExpToNext(int expToNext) { this.expToNext = expToNext; }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }

    public int getDef() { return def; }
    public void setDef(int def) { this.def = def; }

    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getTeleportVisits() { return teleportVisits; }
    public void setTeleportVisits(int teleportVisits) { this.teleportVisits = teleportVisits; }

    public boolean isInCombat() { return inCombat; }
    public void setInCombat(boolean inCombat) { this.inCombat = inCombat; }

    public String getEnemyName() { return enemyName; }
    public void setEnemyName(String enemyName) { this.enemyName = enemyName; }

    public int getEnemyHp() { return enemyHp; }
    public void setEnemyHp(int enemyHp) { this.enemyHp = enemyHp; }

    public int getEnemyMaxHp() { return enemyMaxHp; }
    public void setEnemyMaxHp(int enemyMaxHp) { this.enemyMaxHp = enemyMaxHp; }

    public int getEnemyAtk() { return enemyAtk; }
    public void setEnemyAtk(int enemyAtk) { this.enemyAtk = enemyAtk; }

    public int getEnemyDef() { return enemyDef; }
    public void setEnemyDef(int enemyDef) { this.enemyDef = enemyDef; }

    public int getEnemyLevel() { return enemyLevel; }
    public void setEnemyLevel(int enemyLevel) { this.enemyLevel = enemyLevel; }

    public String getTimeDisplay() { return timeDisplay; }
    public void setTimeDisplay(String timeDisplay) { this.timeDisplay = timeDisplay; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public int getDayCount() { return dayCount; }
    public void setDayCount(int dayCount) { this.dayCount = dayCount; }

    public String getEndingTitle() { return endingTitle; }
    public void setEndingTitle(String endingTitle) { this.endingTitle = endingTitle; }
    public String getEndingDesc() { return endingDesc; }
    public void setEndingDesc(String endingDesc) { this.endingDesc = endingDesc; }
    public int getFinalScore() { return finalScore; }
    public void setFinalScore(int finalScore) { this.finalScore = finalScore; }
    public int getStepCount() { return stepCount; }
    public void setStepCount(int stepCount) { this.stepCount = stepCount; }
    public int getItemsCollected() { return itemsCollected; }
    public void setItemsCollected(int itemsCollected) { this.itemsCollected = itemsCollected; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public void setEnemiesDefeated(int enemiesDefeated) { this.enemiesDefeated = enemiesDefeated; }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public List<String> getQuestList() { return questList; }
    public void setQuestList(List<String> questList) { this.questList = questList; }

    public String getQuestSummary() {
        return questSummary;
    }

    public void setQuestSummary(String questSummary) {
        this.questSummary = questSummary;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
