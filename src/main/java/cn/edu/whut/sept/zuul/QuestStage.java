package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务阶段 —— 任务中的一个步骤，包含若干目标和阶段奖励。
 */
public class QuestStage {
    private int stageIndex;
    private String description;
    private List<QuestObjective> objectives = new ArrayList<>();
    private QuestReward stageReward;

    public QuestStage() {}

    public QuestStage(int stageIndex, String description) {
        this.stageIndex = stageIndex;
        this.description = description;
    }

    /** 检查本阶段所有目标是否都已完成 */
    public boolean isComplete() {
        if (objectives.isEmpty()) {
            return true;
        }
        for (QuestObjective obj : objectives) {
            if (!obj.isComplete()) {
                return false;
            }
        }
        return true;
    }

    /** 获取本阶段进度摘要 */
    public String getProgressSummary() {
        StringBuilder sb = new StringBuilder();
        for (QuestObjective obj : objectives) {
            String typeLabel;
            switch (obj.getType()) {
                case KILL:    typeLabel = "击杀"; break;
                case COLLECT: typeLabel = "收集"; break;
                case TALK:    typeLabel = "对话"; break;
                case EXPLORE: typeLabel = "探索"; break;
                case ANSWER:  typeLabel = "答题"; break;
                case COMBINE: typeLabel = "合成"; break;
                default:      typeLabel = "?"; break;
            }
            sb.append("  [").append(obj.isComplete() ? "✓" : " ")
              .append("] ").append(typeLabel).append(": ")
              .append(obj.getTargetId()).append(" ")
              .append(obj.getProgressText()).append("\n");
        }
        return sb.toString();
    }

    // ========== getters/setters ==========

    public int getStageIndex() { return stageIndex; }
    public void setStageIndex(int stageIndex) { this.stageIndex = stageIndex; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<QuestObjective> getObjectives() { return objectives; }
    public void setObjectives(List<QuestObjective> objectives) { this.objectives = objectives; }

    public QuestReward getStageReward() { return stageReward; }
    public void setStageReward(QuestReward stageReward) { this.stageReward = stageReward; }
}
