package cn.edu.whut.sept.zuul;

/**
 * 排行榜条目：记录玩家一次高分的存档信息。
 */
public class LeaderboardEntry {

    private String playerName;
    private int score;
    private int health;
    private boolean victory;
    private String savedAt;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String playerName, int score, int health,
                            boolean victory, String savedAt) {
        this.playerName = playerName;
        this.score = score;
        this.health = health;
        this.victory = victory;
        this.savedAt = savedAt;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }
}
