package cn.edu.whut.sept.zuul;

import java.util.List;

/**
 * quests 命令 —— 显示所有活跃任务的详细日志。
 */
public class QuestsCommand extends Command {
    @Override
    public boolean execute(Game game) {
        QuestEngine qe = game.getQuestEngine();
        System.out.println("========== 任务日志 ==========");

        for (Quest quest : qe.getVisibleQuests()) {
            int stageIdx = qe.getActiveStageIndex(quest.getId(), game.getQuestProgressMap());
            String statusIcon;
            String stageInfo;

            if (stageIdx < 0) {
                // 检查是否已完成
                String stageKey = game.getQuestProgressMap().get("quest." + quest.getId() + ".stage");
                if ("completed".equals(stageKey)) {
                    statusIcon = "✅";
                    stageInfo = "已完成";
                } else if (stageKey == null) {
                    statusIcon = "⏸";
                    stageInfo = "未启动";
                } else {
                    statusIcon = "⏳";
                    stageInfo = "进行中";
                }
            } else {
                statusIcon = "📋";
                QuestStage stage = quest.getCurrentStage(stageIdx);
                stageInfo = "阶段 " + (stageIdx + 1) + "/" + quest.getTotalStages();
            }

            System.out.println(statusIcon + " [" + quest.getType() + "] " + quest.getName()
                    + " — " + stageInfo);
            System.out.println("  " + quest.getDescription());

            if (stageIdx >= 0) {
                QuestStage stage = quest.getCurrentStage(stageIdx);
                if (stage != null) {
                    System.out.println("  当前: " + stage.getDescription());
                    System.out.print(stage.getProgressSummary());
                }
            }
            System.out.println();
        }

        System.out.println("==============================");
        return false;
    }
}
