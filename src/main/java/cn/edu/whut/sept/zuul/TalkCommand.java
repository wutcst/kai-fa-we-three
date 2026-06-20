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
        String condition = determineCondition(game, npcName);
        String dialogue = game.getCurrentRoom().getNPCDialogue(npcName, condition);

        if (dialogue == null) {
            System.out.println("这个房间里没有这个NPC！");
            game.setHp(game.getHp() - GameConstants.HP_LOSS_TALK_FAIL);
            System.out.println("对话失败，生命值-2，当前HP：" + game.getHp());
        } else {
            System.out.println("[" + npcName + "]：" + dialogue);
            game.setHp(game.getHp() + GameConstants.HP_GAIN_TALK);
            game.addScore(GameConstants.SCORE_GAIN_TALK);
            if ("student".equals(npcName)) {
                game.updateQuestProgress(GameConstants.QUEST_MAIN,
                        GameConstants.QUEST_HINT_RECEIVED);
            }
            System.out.println("对话成功，生命值+5，分数+3，当前HP：" + game.getHp()
                    + "，当前分数：" + game.getScore());
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
        boolean hasTaskItem = playerHasItem(player, "task_item");
        boolean hasKeycard = playerHasItem(player, "keycard");
        boolean hasCookie = playerHasItem(player, "cookie");

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
                break;
            case "student":
                if ("hint_received".equals(mainQuest) && hasTaskItem) {
                    return "has_task_item";
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