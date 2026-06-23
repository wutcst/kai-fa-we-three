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
            System.out.println("成功使用：" + itemName + "！");
            if (itemName.equals("cookie") || itemName.equals("coffee")) {
                String label=itemName.equals("cookie")?"🍪 魔法饼干":"☕ 咖啡";
                System.out.println(label+"生效！最大负重上限+5！");
                game.setHp(game.getHp() + GameConstants.HP_GAIN_EAT_OTHER);
                if(itemName.equals("cookie")){
                    game.addScore(GameConstants.SCORE_GAIN_COOKIE);
                    game.updateQuestProgress("side_quest_cookie", "completed");
                }
                System.out.println("当前HP：" + game.getHp()+"，当前负重上限："+player.getMaxWeight()+"kg");
            } else {
                game.setHp(game.getHp() + GameConstants.HP_GAIN_EAT_OTHER);
                System.out.println("生命值+5，当前HP：" + game.getHp());
            }
        } else {
            System.out.println("无法吃掉这个物品（要么没有，要么不能吃）！");
            game.setHp(game.getHp() - GameConstants.HP_LOSS_EAT_FAIL); // 吃失败扣3HP
            System.out.println("进食失败，生命值-3，当前HP：" + game.getHp());
        }
        return false;
    }
}