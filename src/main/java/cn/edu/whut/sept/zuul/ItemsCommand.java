package cn.edu.whut.sept.zuul;

public class ItemsCommand extends Command {
    @Override
    public boolean execute(Game game) {
        System.out.println("=== 背包状态 ===");
        System.out.println(game.getPlayer().getInventoryString());
        System.out.println("当前生命值：" + game.getHp() + "，当前分数：" + game.getScore());
        System.out.println("任务进度：" + game.formatQuestProgress());
        return false;
    }
}