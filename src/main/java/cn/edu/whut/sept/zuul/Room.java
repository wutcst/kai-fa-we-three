package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room
{
    private String description;
    private Map<String, Room> exits;
    // 房间内物品集合
    private List<Item> items;

    public Room(String desc)
    {
        description = desc;
        exits = new HashMap<>();
        items = new ArrayList<>();
    }

    public void setExit(String dir, Room room)
    {
        exits.put(dir, room);
    }

    public Room getExit(String dir)
    {
        return exits.get(dir);
    }

    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString() + getRoomItemString();
    }

    private String getExitString()
    {
        StringBuilder s = new StringBuilder("Exits:");
        for(String key : exits.keySet()) {
            s.append(" ").append(key);
        }
        return s + "\n";
    }

    // 拼接房间物品信息
    private String getRoomItemString(){
        if(items.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder("Items here: ");
        for(Item item : items){
            sb.append(item.getDescription()).append("  ");
        }
        return sb + "\n";
    }

    // 根据名称获取物品
    public Item getItem(String itemName){
        for(Item item : items){
            if(item.getDescription().equals(itemName)){
                return item;
            }
        }
        return null;
    }

    // 向房间添加物品
    public void addItem(Item item){
        items.add(item);
    }

    // 从房间移除物品
    public void removeItem(Item item){
        items.remove(item);
    }
}