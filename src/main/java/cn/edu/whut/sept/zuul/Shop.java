package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;

/**
 * 商店 —— 绑定到特定商人 NPC，管理商品列表。
 */
public class Shop {
    private String merchantNpcName;
    private List<ShopItem> inventory = new ArrayList<>();
    private boolean buysItems = true;  // 是否从玩家处收购物品

    public Shop() {}

    public Shop(String merchantNpcName) {
        this.merchantNpcName = merchantNpcName;
    }

    public void addItem(ShopItem item) {
        inventory.add(item);
    }

    /** 按名称查找商品 */
    public ShopItem findItem(String itemName) {
        for (ShopItem item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // ========== getters/setters ==========

    public String getMerchantNpcName() { return merchantNpcName; }
    public void setMerchantNpcName(String merchantNpcName) { this.merchantNpcName = merchantNpcName; }

    public List<ShopItem> getInventory() { return inventory; }
    public void setInventory(List<ShopItem> inventory) { this.inventory = inventory; }

    public boolean isBuysItems() { return buysItems; }
    public void setBuysItems(boolean buysItems) { this.buysItems = buysItems; }
}
