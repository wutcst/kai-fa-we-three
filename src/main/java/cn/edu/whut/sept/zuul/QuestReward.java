package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务奖励 —— 经验/金币/物品/解锁下一任务。
 */
public class QuestReward {
    private int exp;
    private int gold;
    private List<String> items = new ArrayList<>();
    /** 完成后自动激活的下一任务 ID */
    private String nextQuestId;

    public QuestReward() {}

    public QuestReward(int exp, int gold) {
        this.exp = exp;
        this.gold = gold;
    }

    // ========== getters/setters ==========

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }

    public String getNextQuestId() { return nextQuestId; }
    public void setNextQuestId(String nextQuestId) { this.nextQuestId = nextQuestId; }
}
