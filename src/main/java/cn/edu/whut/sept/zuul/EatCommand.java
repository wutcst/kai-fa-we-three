package cn.edu.whut.sept.zuul;

public class EatCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Eat what?（请指定要吃的物品名称）");
            return false;
        }

        String itemName = getSecondWord();
        Player player = game.getPlayer();
        boolean success = player.eat(itemName);

        if (success) {
            System.out.println("成功吃掉：" + itemName + "！");
            if (itemName.equals("cookie")) {
                System.out.println("魔法饼干生效！最大负重上限+10！");
                game.setHp(game.getHp() + 20); // 吃饼干+20HP
                System.out.println("生命值+20，当前HP：" + game.getHp());
            } else {
                game.setHp(game.getHp() + 5); // 吃其他物品+5HP
                System.out.println("生命值+5，当前HP：" + game.getHp());
            }
        } else {
            System.out.println("无法吃掉这个物品（要么没有，要么不能吃）！");
            game.setHp(game.getHp() - 3); // 吃失败扣3HP
            System.out.println("进食失败，生命值-3，当前HP：" + game.getHp());
        }
        return false;
    }
}