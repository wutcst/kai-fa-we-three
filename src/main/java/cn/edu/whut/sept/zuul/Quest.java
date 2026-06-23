package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务定义 —— 完整描述一个任务及其所有阶段。
 */
public class Quest {
    public enum QuestType { MAIN, SIDE, DAILY, HIDDEN }

    private String id;                  // 唯一标识
    private String name;
    private String description;
    private QuestType type = QuestType.SIDE;
    private List<QuestStage> stages = new ArrayList<>();
    private QuestReward finalReward;
    private boolean repeatable;
    private boolean hidden;

    public Quest() {}

    public Quest(String id, String name, String description, QuestType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /** 获取当前活跃阶段（第一个未完成的阶段） */
    public QuestStage getCurrentStage(int activeStageIndex) {
        if (activeStageIndex < 0 || activeStageIndex >= stages.size()) {
            return null;
        }
        return stages.get(activeStageIndex);
    }

    /** 是否所有阶段都已完成 */
    public boolean isAllStagesComplete(int activeStageIndex) {
        return activeStageIndex >= stages.size();
    }

    /** 获取总阶段数 */
    public int getTotalStages() {
        return stages.size();
    }

    // ========== getters/setters ==========

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public QuestType getType() { return type; }
    public void setType(QuestType type) { this.type = type; }

    public List<QuestStage> getStages() { return stages; }
    public void setStages(List<QuestStage> stages) { this.stages = stages; }

    public QuestReward getFinalReward() { return finalReward; }
    public void setFinalReward(QuestReward finalReward) { this.finalReward = finalReward; }

    public boolean isRepeatable() { return repeatable; }
    public void setRepeatable(boolean repeatable) { this.repeatable = repeatable; }

    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
}
