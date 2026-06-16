package cn.edu.whut.sept.zuul;

public class HelpCommand extends Command
{
    private CommandWords commandWords;

    public HelpCommand(CommandWords words)
    {
        commandWords = words;
    }

    public boolean execute(Game game)
    {
        System.out.println("=== World of Zuul 帮助 ===");
        System.out.println("你在大学校园中探索，收集物品、与 NPC 对话并完成主线任务。");
        System.out.println("主线：拾取 task_item 后回到 outside 即可胜利。");
        System.out.println();
        System.out.println("Your command words are:");
        commandWords.showAll();
        System.out.println();
        System.out.println("当前状态：HP=" + game.getHp()
                + "，分数=" + game.getScore()
                + "，" + game.formatQuestProgress());
        return false;
    }
}
