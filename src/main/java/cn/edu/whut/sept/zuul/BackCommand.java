package cn.edu.whut.sept.zuul;

public class BackCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        game.back(); // 调用 Game 里的回退功能
        return false;
    }
}