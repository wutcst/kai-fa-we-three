package cn.edu.whut.sept.zuul;

public class TakeCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Take what?（请指定要拾取的物品名称）");
            return false;
        }

        String itemName = getSecondWord();
        Room currentRoom = game.getCurrentRoom();
        Item item = currentRoom.removeItem(itemName);

        if (item == null) {
            System.out.println("这个房间里没有这个物品！");
            game.setHp(game.getHp() - 2); // 拾取失败扣2HP
            System.out.println("拾取失败，生命值-2，当前HP：" + game.getHp());
            return false;
        }

        boolean success = game.getPlayer().takeItem(item);
        if (success) {
            System.out.println("成功拾取：" + item.getDescription() + " (重量：" + item.getWeight() + ")");
            game.setHp(game.getHp() + 3); // 拾取成功+3HP
            game.addScore(5);
            if ("task_item".equals(item.getDescription())) {
                game.updateQuestProgress("main_quest", "collected_task_item");
            }
            System.out.println("拾取成功，生命值+3，分数+5，当前HP：" + game.getHp()
                    + "，当前分数：" + game.getScore());
        } else {
            System.out.println("背包超重，无法拾取！");
            currentRoom.addItem(item); // 物品放回房间
            game.setHp(game.getHp() - 3); // 超重扣3HP
            System.out.println("超重失败，生命值-3，当前HP：" + game.getHp());
        }
        return false;
    }
}