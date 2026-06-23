package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Room currentRoom;
    private int maxWeight;          // 最大负重上限
    private int currentWeight;      // 当前背包总重量
    private int level = 1;
    private int exp = 0;
    private int atk = 10;
    private int def = 5;
    private int sp = 50;
    private int gold = 0;           // 金币
    private int hp = GameConstants.INITIAL_HP;
    private int maxHp = GameConstants.INITIAL_HP;
    private int score = 0;
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

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }

    public int getDef() { return def; }
    public void setDef(int def) { this.def = def; }

    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); }  // 无上限，最低为0

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public void addScore(int points) { this.score += points; }

    /**
     * 增加金币。
     * @param amount 金币数量
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * 消费金币，成功返回 true。
     * @param amount 消费金额
     * @return 是否消费成功
     */
    public boolean spendGold(int amount) {
        if (this.gold < amount) {
            return false;
        }
        this.gold -= amount;
        return true;
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

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public List<Item> getInventoryItems() {
        return new ArrayList<>(inventory);
    }

    public void replaceInventory(List<Item> items, int maxWeight, int currentWeight) {
        this.inventory = new ArrayList<>(items);
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
    }

    public void clearInventory() {
        this.inventory = new ArrayList<>();
        this.currentWeight = 0;
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

    /**
     * 吃掉物品的底层逻辑
     * 验收标准: 判定魔法饼干、增加负重上限、并从背包移除
     * @param itemName 要吃的物品名称
     * @return boolean 是否成功吃掉并生效
     */
    public boolean eat(String itemName) {
        if (itemName.equals("cookie") || itemName.equals("coffee")) {
            Item eaten = dropItem(itemName);
            if (eaten != null) {
                this.maxWeight += 5;
                return true;
            }
            return false;
        }
        return false;
    }

    // ==========================================
    // 战斗与升级系统
    // ==========================================

    /**
     * 获得经验值，达到阈值自动升级。
     * @param amount 经验值
     * @return 本次升级的等级数
     */
    public int gainExp(int amount) {
        if (level >= GameConstants.MAX_LEVEL) {
            return 0;
        }
        this.exp += amount;
        int levelsGained = 0;
        while (level < GameConstants.MAX_LEVEL
                && exp >= GameConstants.EXP_THRESHOLDS[level]) {
            levelUp();
            levelsGained++;
        }
        return levelsGained;
    }

    /**
     * 升级：提升属性并消耗对应经验。
     */
    private void levelUp() {
        int requiredExp = GameConstants.EXP_THRESHOLDS[level];
        this.exp -= requiredExp;
        this.level++;
        this.atk += GameConstants.ATK_PER_LEVEL;
        this.def += GameConstants.DEF_PER_LEVEL;
        this.sp += GameConstants.SP_PER_LEVEL;
    }

    /**
     * 获取升级到下一级所需总经验。
     * @return 所需经验值，满级返回 0
     */
    /**
     * 获取当前等级对应的学术称号。
     * @return 称号字符串
     */
    public String getLevelTitle() {
        switch (level) {
            case 1: return "📚 新生";
            case 2: return "🔍 见习探索者";
            case 3: return "💡 青年研究者";
            case 4: return "🎓 资深学者";
            case 5: return "🏆 学术大师";
            default: return "📚 新生";
        }
    }

    public int getExpToNextLevel() {
        if (level >= GameConstants.MAX_LEVEL) {
            return 0;
        }
        return GameConstants.EXP_THRESHOLDS[level];
    }

    /**
     * 检查 SP 是否足够使用技能。
     * @param cost SP 消耗
     * @return 是否足够
     */
    public boolean canUseSkill(int cost) {
        return this.sp >= cost;
    }

    /**
     * 消耗 SP。
     * @param cost SP 消耗量
     * @return 是否消耗成功
     */
    public boolean consumeSp(int cost) {
        if (this.sp < cost) {
            return false;
        }
        this.sp -= cost;
        return true;
    }
}