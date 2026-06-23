package cn.edu.whut.sept.zuul;

import java.util.Map;

public class GoCommand extends Command {
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Go where?");
            return false;
        }

        String direction = getSecondWord();
        Room currentRoom = game.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        // 正门出口门禁：必须拿了校园地图才能离开
        if (currentRoom.getShortDescription().contains("outside the main entrance")
                && nextRoom != null) {
            if (!game.hasItemInInventory("campus_map")) {
                System.out.println("🗺️ 你对校园不熟悉，迷失了方向！先回门口拿【校园地图】吧。");
                return false;
            }
        }

        if (nextRoom == null) {
            System.out.println("There is no door!");
            game.setHp(game.getHp() - GameConstants.HP_LOSS_WALL);
            System.out.println("移动失败，生命值-2，当前HP：" + game.getHp());
        } else {
            // 传送室等级门槛：Lv.2 以上才能进入
            if (nextRoom instanceof TeleportRoom && game.getPlayer().getLevel() < 2) {
                System.out.println("🌀 你感受到一股奇异的时空力量…但你等级不够（需要 Lv.2 见习探索者以上）。");
                return false;
            }
            if (currentRoom instanceof TeleportRoom) {
                System.out.println("🌀 传送室启动！你被随机传送了！");
            }
            if (nextRoom instanceof TeleportRoom) {
                game.incrementTeleportVisits();
            }
            game.moveToRoom(nextRoom);
            game.getWorldState().advanceTime(10);
            game.addStep();
            game.addRoomVisited();
            System.out.println(nextRoom.getLongDescription());
            game.setHp(game.getHp() + GameConstants.HP_GAIN_MOVE);
            System.out.println("移动成功，生命值+1，当前HP：" + game.getHp());

            // 通知任务引擎：探索房间
            Map<String, String> qp = game.getQuestProgressMap();
            QuestEngine qe = game.getQuestEngine();
            String roomDesc = nextRoom.getShortDescription();
            if (qe.advanceObjective("explorer", QuestObjective.ObjectiveType.EXPLORE,
                    roomDesc, qp)) {
                qe.checkAndAdvanceStage("explorer", qp, game.getPlayer());
            }

        }
        return false;
    }
}