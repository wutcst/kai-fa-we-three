package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Iterator;

public class CommandWords {
    private HashMap<String, Command> commands;

    public CommandWords(PlayerRepository playerRepository) {
        commands = new HashMap<>();
        commands.put("go", new GoCommand());
        commands.put("help", new HelpCommand(this));
        commands.put("quit", new QuitCommand());
        commands.put("back", new BackCommand());
        commands.put("take", new TakeCommand());
        commands.put("drop", new DropCommand());
        commands.put("items", new ItemsCommand());
        commands.put("eat", new EatCommand());
        commands.put("look", new LookCommand());
        commands.put("talk", new TalkCommand());
        commands.put("login", new LoginCommand(playerRepository));
    }

    public Command get(String word) {
        return commands.get(word);
    }

    public void showAll() {
        for (Iterator i = commands.keySet().iterator(); i.hasNext(); ) {
            System.out.print(i.next() + "  ");
        }
        System.out.println();
    }
}