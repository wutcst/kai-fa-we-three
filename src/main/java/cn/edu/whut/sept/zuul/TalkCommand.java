package cn.edu.whut.sept.zuul;

/**
 * TalkCommand allows the player to converse with NPCs in the current room.
 * NPC dialogue branches based on quest progress and player inventory.
 */
public class TalkCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Talk to who?（请指定要对话的NPC名称）");
            return false;
        }

        String npcName = getSecondWord();

        // 时间限制：管理员只在 9:00-17:00 上班
        if ("admin".equals(npcName)) {
            int time = game.getWorldState().getGameTimeMinutes();
            if (time < 540 || time >= 1020) {
                System.out.println("管理员已经下班了（上班时间 9:00-17:00），明天再来吧。");
                return false;
            }
        }
        // 神谕者：Lv.2 以上才能对话
        if ("oracle".equals(npcName)) {
            if (game.getPlayer().getLevel() < 2) {
                System.out.println("🌀 [oracle]：你还太年轻了，冒险者…等你成为「见习探索者」(Lv.2) 再来找我吧。");
                return false;
            }
        }
        // 神谕者只在夜晚出现
        if ("oracle".equals(npcName)) {
            int time = game.getWorldState().getGameTimeMinutes();
            if (time >= 360 && time < 1080) {
                System.out.println("神谕者只在夜晚（18:00-6:00）出现…太阳落山后再来吧。");
                return false;
            }
        }

        String condition = determineCondition(game, npcName);
        String dialogue = game.getCurrentRoom().getNPCDialogue(npcName, condition);

        if (dialogue == null) {
            System.out.println("这个房间里没有这个NPC！");
            game.setHp(game.getHp() - GameConstants.HP_LOSS_TALK_FAIL);
            System.out.println("对话失败，生命值-2，当前HP：" + game.getHp());
        } else {
            boolean firstTime = game.markNpcTalked(npcName);
            System.out.println("[" + npcName + "]：" + dialogue);
            if (firstTime) {
                game.setHp(game.getHp() + GameConstants.HP_GAIN_TALK);
                game.addScore(GameConstants.SCORE_GAIN_TALK);
                System.out.println("首次对话！生命值+5，分数+3，当前HP：" + game.getHp()
                        + "，当前分数：" + game.getScore());
            }
            // NPC 好感度
            int affinity = game.recordNpcAffinity(npcName);
            if (affinity == 4) {
                System.out.println("💛 [" + npcName + "] 对你非常信任了！");
            }
            if ("student".equals(npcName)) {
                game.updateQuestProgress(GameConstants.QUEST_MAIN,
                        GameConstants.QUEST_HINT_RECEIVED);
            }
            // 通知任务引擎
            game.getQuestEngine().advanceObjective("oracle_secret",
                    QuestObjective.ObjectiveType.TALK, npcName, game.getQuestProgressMap());
            game.getQuestEngine().checkAndAdvanceStage("oracle_secret",
                    game.getQuestProgressMap(), game.getPlayer());
        }
        return false;
    }

    /**
     * Determine which dialogue branch to use based on game state.
     * Conditions are evaluated in priority order — first match wins.
     */
    private String determineCondition(Game game, String npcName) {
        Player player = game.getPlayer();
        String mainQuest = game.getQuestProgressValue("main_quest");
        String cookieQuest = game.getQuestProgressValue("side_quest_cookie");
        boolean hasTaskItem = playerHasItem(player, "perfect_report");
        boolean hasKeycard = playerHasItem(player, "keycard");
        boolean hasCookie = playerHasItem(player, "cookie");
        boolean hasMap = playerHasItem(player, "campus_map");

        // Highest priority: main quest completed
        if ("completed".equals(mainQuest)) {
            return "main_completed";
        }

        // NPC-specific conditions (evaluated per NPC)
        switch (npcName) {
            case "guard":
                if (hasTaskItem) {
                    return "has_task_item";
                }
                if (hasMap) {
                    return "has_map";
                }
                break;
            case "student":
                if ("hint_received".equals(mainQuest) && hasTaskItem) {
                    return "has_task_item";
                }
                break;
            case "oracle":
                if (game.getNpcAffinity("oracle") >= 2) {
                    return "secret_hint";
                }
                break;
            case "bartender":
                if ("completed".equals(cookieQuest)) {
                    return "ate_cookie";
                }
                if (hasCookie) {
                    return "has_cookie";
                }
                break;
            case "admin":
                if ("hint_received".equals(mainQuest)) {
                    return "hint_received";
                }
                break;
            case "lecturer":
                if ("hint_received".equals(mainQuest)) {
                    return "hint_received";
                }
                break;
            default:
                break;
        }

        // Fallback to default dialogue
        return "default";
    }

    private boolean playerHasItem(Player player, String itemName) {
        for (Item item : player.getInventoryItems()) {
            if (item.getDescription().equals(itemName)) {
                return true;
            }
        }
        return false;
    }
}