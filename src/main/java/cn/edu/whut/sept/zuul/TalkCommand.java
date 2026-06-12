package cn.edu.whut.sept.zuul;

public class TalkCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Talk to who?（请指定要对话的NPC名称）");
            return false;
        }

        String npcName = getSecondWord();
        String dialogue = game.getCurrentRoom().getNPCDialogue(npcName);

        if (dialogue == null) {
            System.out.println("这个房间里没有这个NPC！");
            game.setHp(game.getHp() - 2); // 对话失败扣2HP
            System.out.println("对话失败，生命值-2，当前HP：" + game.getHp());
        } else {
            System.out.println("[" + npcName + "]：" + dialogue);
            game.setHp(game.getHp() + 5); // 对话成功+5HP
            System.out.println("对话成功，生命值+5，当前HP：" + game.getHp());
        }
        return false;
    }
}