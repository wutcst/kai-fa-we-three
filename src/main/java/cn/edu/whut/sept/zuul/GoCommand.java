package cn.edu.whut.sept.zuul;

public class GoCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()) {
            System.out.println("go where?");
            return false;
        }

        String dir = getSecondWord();
        Room nextRoom = game.getCurrentRoom().getExit(dir);

        if(nextRoom == null) {
            System.out.println("No way!");
        } else {
            // 保存当前房间到历史
            game.pushCurrentRoomToHistory();
            game.setCurrentRoom(nextRoom);
            System.out.println(game.getCurrentRoom().getLongDescription());

            // 移动消耗体力，扣除生命值
            game.getPlayer().reduceHp(5);
            System.out.println("移动消耗体力，当前生命值：" + game.getPlayer().getCurrentHp());

            // ========= 移动后检查胜利条件 =========
            if(game.checkWinCondition()){
                // 胜利，返回 true 让主循环 finished = true，结束游戏
                return true;
            }
        }

        return false;
    }
}