package cn.edu.whut.sept.zuul;

public class AnswerCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Answer what? (e.g. answer A)");
            return false;
        }

        String answer = getSecondWord().toUpperCase();
        System.out.println("你回答了：" + answer);

        // 根据房间判断正确答案
        String correctAnswer = "A";  // 默认
        String roomDesc = game.getCurrentRoom().getShortDescription();
        if (roomDesc.contains("admin office")) {
            correctAnswer = "B";  // 管理员：HTTP 404 = 未找到资源
        }

        if (correctAnswer.equals(answer)) {
            System.out.println("✅ 回答正确！导师很满意，允许你拿走物品。");
            game.addScore(10);
            game.setHp(game.getHp() + 5);
            game.getPlayer().addGold(5);
            System.out.println("🪙 获得 5 金币！");
            // Unlock items AND auto-pickup
            Room currentRoom = game.getCurrentRoom();
            for (Item item : new java.util.ArrayList<>(currentRoom.getItems())) {
                if (item.getLockedByNpc() != null) {
                    item.unlock();
                    // 自动拾取到背包
                    boolean taken = game.getPlayer().takeItem(item);
                    if (taken) {
                        currentRoom.removeItem(item.getDescription());
                        System.out.println("✅ " + item.getDescription() + " 已自动放入背包！");
                    } else {
                        System.out.println("⚠ 背包已满，" + item.getDescription() + " 已解锁但仍在地上。");
                    }
                }
            }

            // 通知任务引擎：答题成功
            java.util.Map<String, String> qp = game.getQuestProgressMap();
            QuestEngine qe = game.getQuestEngine();
            if (qe.advanceObjective("main_quest", QuestObjective.ObjectiveType.ANSWER,
                    "lecturer", qp)) {
                qe.checkAndAdvanceStage("main_quest", qp, game.getPlayer());
            }
        } else {
            System.out.println("回答错误！导师摇了摇头。");
            game.setHp(game.getHp() - 5);
        }

        return false;
    }
}
