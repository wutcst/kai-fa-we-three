package cn.edu.whut.sept.zuul;

import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Room
{
    private String description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    private HashMap<String, Item> items;        // stores items in this room.
    private HashMap<String, NPC> npcs;        // stores NPCs in this room.

    public Room(String description)
    {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
        npcs = new HashMap<>();
    }

    public void setExit(String direction, Room neighbor)
    {
        exits.put(direction, neighbor);
    }

    public String getShortDescription()
    {
        return description;
    }

    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString() + "\n"
                + getItemsString() + "\n" + getNPCsString();
    }

    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    private String getItemsString()
    {
        if (items.isEmpty()) {
            return "There are no items here.";
        }
        StringBuilder returnString = new StringBuilder("Items in this room:");
        for (Item item : items.values()) {
            returnString.append("\n - ").append(item.getDescription())
                    .append(" (重量: ").append(item.getWeight()).append(")");
        }
        return returnString.toString();
    }

    private String getNPCsString()
    {
        if (npcs.isEmpty()) {
            return "There is no one else here.";
        }
        StringBuilder returnString = new StringBuilder("People in this room:");
        for (NPC npc : npcs.values()) {
            returnString.append("\n - ").append(npc.getName());
        }
        return returnString.toString();
    }

    public Room getExit(String direction)
    {
        return exits.get(direction);
    }

    public List<String> getExitDirections()
    {
        return new ArrayList<>(exits.keySet());
    }
    public void addItem(Item item)
    {
        items.put(item.getDescription(), item);
    }

    public Item removeItem(String itemName)
    {
        return items.remove(itemName);
    }

    public List<Item> getItems()
    {
        return new ArrayList<>(items.values());
    }

    public void clearItems()
    {
        items.clear();
    }

    public void addNPC(NPC npc)
    {
        npcs.put(npc.getName(), npc);
    }

    public List<String> getNpcNames()
    {
        return new ArrayList<>(npcs.keySet());
    }

    public String getNPCDialogue(String npcName)
    {
        NPC npc = npcs.get(npcName);
        if (npc == null) {
            return null;
        }
        return npc.getDialogue();
    }

    /**
     * Get NPC dialogue matching a specific condition (e.g. quest progress).
     * Falls back to default dialogue if the condition has no match.
     */
    public String getNPCDialogue(String npcName, String condition)
    {
        NPC npc = npcs.get(npcName);
        if (npc == null) {
            return null;
        }
        return npc.getDialogue(condition);
    }
}


