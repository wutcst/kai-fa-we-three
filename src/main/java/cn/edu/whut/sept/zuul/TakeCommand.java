package cn.edu.whut.sept.zuul;

public class TakeCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()){
            System.out.println("请输入要拾取的物品名称！");
            return true;
        }
        String itemName = getSecondWord();
        Room currentRoom = game.getCurrentRoom();
        Item targetItem = currentRoom.getItem(itemName);

        if(targetItem == null){
            System.out.println("当前场景没有该物品！");
            return true;
        }

        boolean takeOk = game.getPlayer().takeItem(targetItem);
        if(takeOk){
            // 拾取成功后从房间移除物品
            currentRoom.removeItem(targetItem);
            System.out.println("成功拾取：" + itemName);
        }else{
            System.out.println("背包负重已满，无法拾取！");
        }
        return true;
    }
}