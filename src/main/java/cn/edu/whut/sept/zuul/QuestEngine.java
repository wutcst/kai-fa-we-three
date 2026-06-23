package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态任务引擎 —— 管理任务模板、活跃任务和进度推进。
 *
 * 活跃任务状态存储为：
 *   questProgress["quest.<id>.stage"] = "0"    (当前阶段索引)
 *   questProgress["quest.<id>.obj.<type>.<targetId>"] = "2"  (目标进度计数)
 *
 * 由于需要与现有 questProgress Map 兼容（String→String），
 * 任务进度以特殊前缀的键存储。
 */
public class QuestEngine {

    private static final String KEY_PREFIX = "quest.";
    private static final String STAGE_SUFFIX = ".stage";
    private static final String OBJ_SUFFIX = ".obj.";

    /** 所有已加载的任务模板 */
    private final Map<String, Quest> questTemplates = new LinkedHashMap<>();

    /**
     * 注册一个任务模板。
     */
    public void registerQuest(Quest quest) {
        questTemplates.put(quest.getId(), quest);
    }

    /**
     * 获取所有非隐藏任务。
     */
    public List<Quest> getVisibleQuests() {
        List<Quest> visible = new ArrayList<>();
        for (Quest q : questTemplates.values()) {
            if (!q.isHidden()) {
                visible.add(q);
            }
        }
        return visible;
    }

    /**
     * 获取任务模板。
     */
    public Quest getQuest(String questId) {
        return questTemplates.get(questId);
    }

    // ========== 活跃任务管理 ==========

    /**
     * 启动一个任务（写入 questProgress）。
     */
    public boolean startQuest(String questId, Map<String, String> questProgress) {
        Quest quest = questTemplates.get(questId);
        if (quest == null) {
            return false;
        }
        String stageKey = makeStageKey(questId);
        if (!questProgress.containsKey(stageKey)) {
            questProgress.put(stageKey, "0");
            return true;
        }
        return false; // 已经启动
    }

    /**
     * 获取任务的当前阶段索引。
     * @return -1 表示任务尚未启动或已完成
     */
    public int getActiveStageIndex(String questId, Map<String, String> questProgress) {
        String stageKey = makeStageKey(questId);
        String val = questProgress.get(stageKey);
        if (val == null) {
            return -1;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取当前活跃阶段，null 表示已全部完成或未启动。
     */
    public QuestStage getActiveStage(String questId, Map<String, String> questProgress) {
        Quest quest = questTemplates.get(questId);
        if (quest == null) return null;
        int idx = getActiveStageIndex(questId, questProgress);
        return quest.getCurrentStage(idx);
    }

    /**
     * 推进一个目标。
     * @return 该目标是否恰好在这一步完成
     */
    public boolean advanceObjective(String questId, QuestObjective.ObjectiveType type,
                                     String targetId, Map<String, String> questProgress) {
        Quest quest = questTemplates.get(questId);
        if (quest == null) return false;

        int stageIdx = getActiveStageIndex(questId, questProgress);
        QuestStage stage = quest.getCurrentStage(stageIdx);
        if (stage == null) return false;

        for (QuestObjective obj : stage.getObjectives()) {
            if (obj.getType() == type && obj.getTargetId().equals(targetId)) {
                if (!obj.isComplete()) {
                    obj.advance();
                    // 持久化进度
                    String objKey = makeObjKey(questId, type.name(), targetId);
                    questProgress.put(objKey, String.valueOf(obj.getCurrentCount()));
                    return obj.isComplete();
                }
            }
        }
        return false;
    }

    /**
     * 检查当前阶段是否完成，若完成则推进到下一阶段并发放奖励。
     * @return 阶段奖励（如果阶段刚完成），null 表示未完成
     */
    public QuestReward checkAndAdvanceStage(String questId, Map<String, String> questProgress,
                                             Player player) {
        Quest quest = questTemplates.get(questId);
        if (quest == null) return null;

        int stageIdx = getActiveStageIndex(questId, questProgress);
        QuestStage stage = quest.getCurrentStage(stageIdx);
        if (stage == null) return null;

        if (!stage.isComplete()) {
            return null;
        }

        // 发放阶段奖励
        QuestReward reward = stage.getStageReward();
        if (reward != null) {
            applyReward(reward, player);
        }

        // 推进到下一阶段
        int nextStage = stageIdx + 1;
        questProgress.put(makeStageKey(questId), String.valueOf(nextStage));

        // 如果全部阶段完成，发放最终奖励
        if (nextStage >= quest.getTotalStages()) {
            QuestReward finalReward = quest.getFinalReward();
            if (finalReward != null) {
                applyReward(finalReward, player);
            }

            // 标记完成
            questProgress.put(makeStageKey(questId), "completed");

            // 解锁下一任务
            if (finalReward != null && finalReward.getNextQuestId() != null) {
                startQuest(finalReward.getNextQuestId(), questProgress);
            }
            return finalReward;
        }

        return reward;
    }

    /**
     * 加载存档时恢复目标进度。
     */
    public void restoreProgress(String questId, Map<String, String> questProgress) {
        Quest quest = questTemplates.get(questId);
        if (quest == null) return;

        int stageIdx = getActiveStageIndex(questId, questProgress);
        if (stageIdx < 0) return;

        QuestStage stage = quest.getCurrentStage(stageIdx);
        if (stage == null) return;

        for (QuestObjective obj : stage.getObjectives()) {
            String objKey = makeObjKey(questId, obj.getType().name(), obj.getTargetId());
            String val = questProgress.get(objKey);
            if (val != null) {
                try {
                    obj.setCurrentCount(Integer.parseInt(val));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    /**
     * 获取所有活跃任务的摘要（用于前端展示）。
     */
    public List<String> getQuestSummaryList(Map<String, String> questProgress) {
        List<String> summaries = new ArrayList<>();
        for (Quest quest : questTemplates.values()) {
            int idx = getActiveStageIndex(quest.getId(), questProgress);
            if (idx < 0 && !questProgress.containsKey(makeStageKey(quest.getId()))) {
                continue; // 未启动
            }
            String status;
            if (idx < 0) {
                status = "✅ 已完成";
            } else {
                QuestStage stage = quest.getCurrentStage(idx);
                status = stage != null ? "📋 阶段 " + (idx + 1) + "/" + quest.getTotalStages() : "⏳";
            }
            summaries.add(quest.getName() + " [" + status + "]");
        }
        return summaries;
    }

    // ========== 辅助方法 ==========

    private void applyReward(QuestReward reward, Player player) {
        if (reward.getExp() > 0) {
            player.gainExp(reward.getExp());
        }
        if (reward.getGold() > 0) {
            player.addGold(reward.getGold());
        }
        for (String itemName : reward.getItems()) {
            player.takeItem(new Item(itemName, 1));
        }
    }

    private String makeStageKey(String questId) {
        return KEY_PREFIX + questId + STAGE_SUFFIX;
    }

    private String makeObjKey(String questId, String type, String targetId) {
        return KEY_PREFIX + questId + OBJ_SUFFIX + type + "." + targetId;
    }
}
