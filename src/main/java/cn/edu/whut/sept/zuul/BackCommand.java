package cn.edu.whut.sept.zuul;

public class BackCommand extends Command {
    @Override
    public boolean execute(Game game) {
        try {
            Room prevRoom = game.popLastRoom();
            if (prevRoom == null) {
                System.out.println("已经回到游戏起点，无法再后退！");
            } else {
                game.setCurrentRoom(prevRoom);
                System.out.println("回到了上一个房间：");
                System.out.println(game.getCurrentRoom().getLongDescription());
            }
        } catch (Exception e) {
            System.out.println("已经回到游戏起点，无法再后退！");
        }
        return false;
    }
}