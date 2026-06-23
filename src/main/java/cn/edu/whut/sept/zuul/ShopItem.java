package cn.edu.whut.sept.zuul;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 商店商品 —— 可在商人处买卖的物品。
 */
public class ShopItem {
    public enum ItemType { WEAPON, ARMOR, CONSUMABLE, KEY_ITEM }

    private String name;
    private int price;
    private int sellPrice;          // 卖回价格（通常为买价的 50%）
    private ItemType type;
    private String description;
    /** 属性加成：atk/def/hp/sp */
    private final Map<String, Integer> stats = new LinkedHashMap<>();

    public ShopItem() {}

    public ShopItem(String name, int price, ItemType type) {
        this.name = name;
        this.price = price;
        this.sellPrice = price / 2;
        this.type = type;
    }

    public ShopItem withStat(String stat, int value) {
        this.stats.put(stat, value);
        return this;
    }

    public int getStatValue(String statName) {
        return stats.getOrDefault(statName, 0);
    }

    // ========== getters/setters ==========

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getSellPrice() { return sellPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }

    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Integer> getStats() { return stats; }
}
