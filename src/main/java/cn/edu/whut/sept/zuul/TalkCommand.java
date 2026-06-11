package cn.edu.whut.sept.zuul;

public class TalkCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()){
            System.out.println("请指定对话对象！例：talk npc");
            return true;
        }
        System.out.println("你与 " + getSecondWord() + " 交谈：");
        System.out.println("对方：冒险路上请留意生命值哦！");
        return true;
    }
}