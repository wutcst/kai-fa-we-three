package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Room currentRoom;
    private int maxWeight;          // 最大负重上限
    private int currentWeight;      // 当前背包总重量
    private List<Item> inventory;   // 随身物品集合 (背包)

    /**
     * 玩家构造函数
     * @param name 玩家姓名
     * @param maxWeight 玩家最大负重能力
     */
    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.inventory = new ArrayList<>();
    }

    // ==========================================
    // 基础 Getter 和 Setter
    // ==========================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    // ==========================================
    // 核心业务逻辑 (严格遵循验收标准与接口约定)
    // ==========================================

    /**
     * 拾取物品
     * 验收标准: 判断拾取新物件时是否超过负重上限，超重则返回失败状态
     * @param item 要拾取的物品对象
     * @return boolean 是否拾取成功
     */
    public boolean takeItem(Item item) {
        if (this.currentWeight + item.getWeight() > this.maxWeight) {
            return false; // 超重，交由 Game 层处理打印提示
        }

        this.inventory.add(item);
        this.currentWeight += item.getWeight();
        return true;
    }

    /**
     * 丢弃物品
     * 验收标准: 根据物品名称从随身集合中移除该物品
     * @param itemName 要丢弃的物品名称
     * @return Item 被丢弃的物品对象（如果背包里没有该物品，则返回 null）
     */
    public Item dropItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            if (item.getDescription().equals(itemName)) {
                inventory.remove(i);
                this.currentWeight -= item.getWeight();
                return item;
            }
        }
        return null;
    }

    /**
     * 查询背包当前状态
     * 验收标准: 返回当前背包所有物品的详细信息和总重量
     * @return String 背包信息的格式化字符串
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "你现在背包里空空如也。";
        }

        StringBuilder returnString = new StringBuilder("你随身携带的物品:\n");
        for (Item item : inventory) {
            returnString.append("  - ").append(item.getDescription())
                    .append(" (重量: ").append(item.getWeight()).append(")\n");
        }
        returnString.append("当前负重: ").append(currentWeight).append(" / ").append(maxWeight);

        return returnString.toString();
    }
}