package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Iterator;

public class CommandWords
{
    private HashMap<String, Command> commands;

    public CommandWords()
    {
        commands = new HashMap<String, Command>();
        // 原有命令
        commands.put("go", new GoCommand());
        commands.put("help", new HelpCommand(this));
        commands.put("quit", new QuitCommand());
        commands.put("back", new BackCommand());

        // 新增要求命令
        commands.put("take", new TakeCommand());
        commands.put("drop", new DropCommand());
        commands.put("items", new ItemsCommand());
        commands.put("eat", new EatCommand());
        commands.put("look", new LookCommand());
        commands.put("talk", new TalkCommand());
    }

    public Command get(String word)
    {
        return commands.get(word);
    }

    public void showAll()
    {
        for(Iterator i = commands.keySet().iterator(); i.hasNext(); ) {
            System.out.print(i.next() + "  ");
        }
        System.out.println();
    }
}