package cn.edu.whut.sept.zuul;

public class DropCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()){
            System.out.println("请输入要丢弃的物品名称！");
            return true;
        }
        String itemName = getSecondWord();
        Item dropItem = game.getPlayer().dropItem(itemName);

        if(dropItem == null){
            System.out.println("背包中没有该物品！");
        }else{
            // 丢弃物品放回当前房间
            game.getCurrentRoom().addItem(dropItem);
            System.out.println("成功丢弃：" + itemName);
        }
        return true;
    }
}