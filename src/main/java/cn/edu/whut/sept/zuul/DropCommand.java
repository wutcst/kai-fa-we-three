package cn.edu.whut.sept.zuul;

public class DropCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Drop what?（请指定要丢弃的物品名称）");
            return false;
        }

        String itemName = getSecondWord();
        Player player = game.getPlayer();
        Item droppedItem = player.dropItem(itemName);

        if (droppedItem == null) {
            System.out.println("背包里没有这个物品！");
            game.setHp(game.getHp() - 2); // 丢弃失败扣2HP
            System.out.println("丢弃失败，生命值-2，当前HP：" + game.getHp());
            return false;
        }

        game.getCurrentRoom().addItem(droppedItem);
        System.out.println("成功丢弃：" + droppedItem.getDescription() + " (重量：" + droppedItem.getWeight() + ")");
        game.setHp(game.getHp() + 2); // 丢弃成功+2HP
        System.out.println("丢弃成功，生命值+2，当前HP：" + game.getHp());
        return false;
    }
}