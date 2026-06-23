package cn.edu.whut.sept.zuul;

/**
 * status 命令 —— 显示完整的角色属性面板。
 */
public class StatusCommand extends Command {
    @Override
    public boolean execute(Game game) {
        Player p = game.getPlayer();
        System.out.println("========== 角色状态 ==========");
        System.out.println("姓名: " + p.getName());
        System.out.println("称号: " + p.getLevelTitle() + " (Lv." + p.getLevel() + ")");
        System.out.println("经验: " + p.getExp() + " / " + p.getExpToNextLevel());
        System.out.println("HP: " + p.getHp() + " / " + p.getMaxHp());
        System.out.println("金币: " + p.getGold());
        System.out.println("背包负重: " + p.getCurrentWeight() + " / " + p.getMaxWeight());
        System.out.println("分数: " + p.getScore());
        System.out.println("==============================");
        return false;
    }
}
