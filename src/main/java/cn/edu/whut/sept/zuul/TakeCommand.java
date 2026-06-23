package cn.edu.whut.sept.zuul;

import java.util.Map;

public class TakeCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Take what?（请指定要拾取的物品名称）");
            return false;
        }

        String itemName = getSecondWord();
        Room currentRoom = game.getCurrentRoom();

        // 办公室时间门禁：非工作时间所有物品不能拿
        if (currentRoom.getShortDescription().contains("admin office")) {
            int time = game.getWorldState().getGameTimeMinutes();
            if (time < 540 || time >= 1020) {
                System.out.println("⏰ 管理员下班了（9:00-17:00），办公室物品暂时无法取用。");
                return false;
            }
        }

        Item item = currentRoom.removeItem(itemName);

        if (item == null) {
            System.out.println("这个房间里没有这个物品！");
            game.setHp(game.getHp() - GameConstants.HP_LOSS_TALK_FAIL);
            System.out.println("拾取失败，生命值-2，当前HP：" + game.getHp());
            return false;
        }

        if (item.isLocked()) {
            String locker = item.getLockedByNpc();
            // keycard 特殊处理：检查背包 + 触发答题
            if ("keycard".equals(locker)) {
                boolean hasKeycard = false;
                for (Item invItem : game.getPlayer().getInventoryItems()) {
                    if ("keycard".equals(invItem.getDescription())) { hasKeycard = true; break; }
                }
                if (!hasKeycard) {
                    System.out.println("🔒 需要门禁卡(keycard)才能提取 code_data！去办公室找管理员答题获取。");
                    currentRoom.addItem(item);
                    return false;
                }
                // 有 keycard，直接解锁
                item.unlock();
            } else {
                System.out.println("物品被 " + locker + " 守护着，你需要先与TA交谈解答提问！");
                currentRoom.addItem(item);
                return false;
            }
        }

        boolean success = game.getPlayer().takeItem(item);
        if (success) {
            System.out.println("成功拾取：" + item.getDescription() + " (重量：" + item.getWeight() + ")");
            game.setHp(game.getHp() + GameConstants.HP_GAIN_TAKE);
            game.addScore(5);
            game.addItemCollected();
            if ("task_item".equals(item.getDescription())) {
                game.updateQuestProgress(GameConstants.QUEST_MAIN,
                        GameConstants.QUEST_COLLECTED_TASK_ITEM);
            }
            System.out.println("拾取成功，生命值+3，分数+5，当前HP：" + game.getHp()
                    + "，当前分数：" + game.getScore());

            // 通知任务引擎：收集物品
            Map<String, String> qp = game.getQuestProgressMap();
            QuestEngine qe = game.getQuestEngine();
            for (String questId : new String[]{"main_quest", "side_cookie"}) {
                if (qe.advanceObjective(questId, QuestObjective.ObjectiveType.COLLECT,
                        item.getDescription(), qp)) {
                    qe.checkAndAdvanceStage(questId, qp, game.getPlayer());
                }
            }
        } else {
            System.out.println("背包超重，无法拾取！");
            currentRoom.addItem(item); // 物品放回房间
            game.setHp(game.getHp() - GameConstants.HP_LOSS_OVERWEIGHT);
            System.out.println("超重失败，生命值-3，当前HP：" + game.getHp());
        }
        return false;
    }
}