package cn.edu.whut.sept.zuul;

import java.util.Set;
import java.util.HashMap;

public class Room
{
    private String description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    private HashMap<String, Item> items;        // stores items in this room.

    public Room(String description)
    {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
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
        return "You are " + description + ".\n" + getExitString() + "\n" + getItemsString();
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

    public Room getExit(String direction)
    {
        return exits.get(direction);
    }
    public void addItem(Item item)
    {
        items.put(item.getDescription(), item);
    }

    public Item removeItem(String itemName)
    {
        return items.remove(itemName);
    }
}


