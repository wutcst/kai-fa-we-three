package cn.edu.whut.sept.zuul;

public class EatCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()){
            System.out.println("请输入要食用的物品名称！");
            return true;
        }
        String itemName = getSecondWord();
        Player player = game.getPlayer();

        if(!player.hasItem(itemName)){
            System.out.println("背包中没有该物品！");
            return true;
        }

        // 分支1：吃魔法饼干（原有逻辑，增加负重）
        if(itemName.contains("cookie")){
            boolean eatOk = player.eatCookie(itemName);
            if(eatOk){
                System.out.println("食用魔法饼干成功，负重上限 +20！");
            }
        }
        // 分支2：吃普通食物（苹果）恢复生命值
        else if("apple".equals(itemName)){
            // 先移除物品，再回血
            player.dropItem(itemName);
            String hpMsg = player.recoverHp(20);
            System.out.println(hpMsg);
        }
        // 其他物品无法食用
        else{
            System.out.println("该物品不能食用！");
        }
        return true;
    }
}