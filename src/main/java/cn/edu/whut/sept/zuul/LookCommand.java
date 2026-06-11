package cn.edu.whut.sept.zuul;

public class LookCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        System.out.println("你环顾四周查看环境：");
        System.out.println(game.getRoomInfo());
        return true;
    }
}