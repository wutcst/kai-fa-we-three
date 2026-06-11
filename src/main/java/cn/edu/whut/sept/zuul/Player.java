package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Room currentRoom;
    private int maxWeight;        // 最大负重上限
    private int currentWeight;    // 当前背包总重量
    private List<Item> inventory; // 随身物品集合 (背包)
    private int maxHp;    // 最大生命值
    private int currentHp;// 当前生命值
    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.inventory = new ArrayList<>();
        this.maxHp = 100;
        this.currentHp = 100;
    }

    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public Room getCurrentRoom() { return currentRoom; }

    /**
     * 获取当前生命值
     */
    public int getCurrentHp() {
        return currentHp;
    }

    /**
     * 获取最大生命值
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * 扣除生命值（移动、受伤调用）
     * @param value 扣血量
     */
    public void reduceHp(int value) {
        currentHp = Math.max(0, currentHp - value);
    }

    /**
     * 恢复生命值（eat 指令食用回血物品调用）
     * @param value 回血量
     * @return 提示文本
     */
    public String recoverHp(int value) {
        if (currentHp >= maxHp) {
            return "生命值已满，无需恢复！当前HP：" + currentHp + "/" + maxHp;
        }
        currentHp = Math.min(maxHp, currentHp + value);
        return "食用成功，恢复 " + value + " 点生命值，当前HP：" + currentHp + "/" + maxHp;
    }

    /**
     * 判断背包中是否存在指定名称的物品
     * @param itemName 物品名称
     * @return 存在返回true，不存在返回false
     */
    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getDescription().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 尝试拾取物品 (take)
     * @return 拾取成功返回 true，超重返回 false
     */
    public boolean takeItem(Item item) {
        if (this.currentWeight + item.getWeight() > this.maxWeight) {
            return false; // 超重，拒绝拾取
        }
        inventory.add(item);
        this.currentWeight += item.getWeight();
        return true;
    }

    /**
     * 尝试丢弃物品 (drop)
     * @return 如果背包有该物品，移除并返回对象；否则返回 null
     */
    public Item dropItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            if (item.getDescription().equals(itemName)) {
                inventory.remove(i);
                this.currentWeight -= item.getWeight(); // 扣除重量
                return item;
            }
        }
        return null;
    }

    /**
     * 吃魔法饼干增加负重 (eat cookie)
     * @return 吃成功返回 true，没找到返回 false
     */
    public boolean eatCookie(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            // 判断确实是我们要吃的那个物品，并且它带有 cookie 关键字
            if (item.getDescription().equals(itemName) && itemName.contains("cookie")) {
                inventory.remove(i); // 吃掉消耗
                this.currentWeight -= item.getWeight(); // 减去饼干本身的重量
                this.maxWeight += 20; // 永久增加 20 点负重上限
                return true;
            }
        }
        return false;
    }

    /**
     * 查看背包内容 (items)
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "你的背包空空如也。（当前负重: " + currentWeight + " / " + maxWeight + "）";
        }
        StringBuilder returnString = new StringBuilder("你随身携带的物品有:\n");
        for (Item item : inventory) {
            returnString.append(" - ").append(item.getDescription())
                    .append(" (重量: ").append(item.getWeight()).append(")\n");
        }
        returnString.append("当前总负重: ").append(currentWeight).append(" / ").append(maxWeight);
        returnString.append("当前生命值：").append(currentHp).append("/").append(maxHp);
        return returnString.toString();
    }

    // --- 本地自测入口 ---
    public static void main(String[] args) {
        Player testPlayer = new Player("测试英雄", 50);
        Item sword = new Item("sword", 30);
        Item magicCookie = new Item("magic cookie", 5);

        System.out.println("1. 初始状态：\n" + testPlayer.getInventoryString() + "\n");

        System.out.println("2. 拾取长剑(30)和魔法饼干(5)：");
        testPlayer.takeItem(sword);
        testPlayer.takeItem(magicCookie);
        System.out.println(testPlayer.getInventoryString() + "\n");

        System.out.println("3. 尝试吃掉魔法饼干：");
        boolean eatSuccess = testPlayer.eatCookie("magic cookie");
        System.out.println("吃饼干是否成功: " + eatSuccess);
        System.out.println(testPlayer.getInventoryString() + "\n");

        System.out.println("4. 扣除10点生命值：");
        testPlayer.reduceHp(10);
        System.out.println("当前HP：" + testPlayer.getCurrentHp());

        System.out.println("5. 食用苹果回血：");
        String hpMsg = testPlayer.recoverHp(20);
        System.out.println(hpMsg);
    }
}