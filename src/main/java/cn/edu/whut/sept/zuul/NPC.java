package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Map;

/**
 * NPC represents a non-player character in the game.
 * Supports branching dialogues that change based on game state conditions.
 */
public class NPC
{
    private String name;
    private final Map<String, String> dialogues = new HashMap<>();

    /**
     * Create an NPC with a default dialogue (shown when no condition matches).
     */
    public NPC(String name, String dialogue)
    {
        this.name = name;
        this.dialogues.put("default", dialogue);
    }

    /**
     * Add a conditional dialogue that triggers when the given condition is met.
     * Conditions are evaluated in TalkCommand based on quest progress, inventory, etc.
     */
    public void addDialogue(String condition, String dialogue)
    {
        this.dialogues.put(condition, dialogue);
    }

    public String getName()
    {
        return name;
    }

    /**
     * Return the default dialogue (backward compatible).
     */
    public String getDialogue()
    {
        return dialogues.get("default");
    }

    /**
     * Return the dialogue matching the given condition, falling back to default.
     */
    public String getDialogue(String condition)
    {
        if (condition != null && dialogues.containsKey(condition))
        {
            return dialogues.get(condition);
        }
        return dialogues.get("default");
    }
}
