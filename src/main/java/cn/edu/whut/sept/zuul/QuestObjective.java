package cn.edu.whut.sept.zuul;

/**
 * 任务目标 —— 描述完成任务阶段需要达成的条件。
 */
public class QuestObjective {
    public enum ObjectiveType {
        KILL,       // 击杀指定敌人
        COLLECT,    // 收集指定物品
        TALK,       // 与指定 NPC 对话
        EXPLORE,    // 到达指定房间
        ANSWER,     // 正确回答 NPC 问题
        COMBINE     // 合成指定物品
    }

    private ObjectiveType type;
    private String targetId;        // 敌人名/物品名/NPC名/房间名
    private int requiredCount;      // 需要数量
    private int currentCount;       // 当前进度

    public QuestObjective() {}

    public QuestObjective(ObjectiveType type, String targetId, int requiredCount) {
        this.type = type;
        this.targetId = targetId;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    /** 推进进度，返回是否已完成 */
    public boolean advance() {
        if (currentCount < requiredCount) {
            currentCount++;
        }
        return isComplete();
    }

    public boolean isComplete() {
        return currentCount >= requiredCount;
    }

    public String getProgressText() {
        return currentCount + "/" + requiredCount;
    }

    // ========== getters/setters ==========

    public ObjectiveType getType() { return type; }
    public void setType(ObjectiveType type) { this.type = type; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public int getRequiredCount() { return requiredCount; }
    public void setRequiredCount(int requiredCount) { this.requiredCount = requiredCount; }

    public int getCurrentCount() { return currentCount; }
    public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }
}
