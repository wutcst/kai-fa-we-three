package cn.edu.whut.sept.zuul;

public class ItemsCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        System.out.println(game.getPlayer().getInventoryString());
        return true;
    }
}