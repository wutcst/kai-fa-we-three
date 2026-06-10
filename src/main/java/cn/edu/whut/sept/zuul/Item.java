package cn.edu.whut.sept.zuul;

public class Item {
    private String description; // 物品的描述/名称
    private int weight;         // 物品的重量 (负重拦截的核心依赖)

    /**
     * 物品的构造函数
     * @param description 物品的描述 (例如 "sword", "cookie")
     * @param weight 物品的重量 (例如 5, 1)
     */
    public Item(String description, int weight) {
        this.description = description;
        this.weight = weight;
    }

    /**
     * 获取物品描述
     * @return 物品的字符串描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取物品重量
     * @return 物品的重量值
     */
    public int getWeight() {
        return weight;
    }
}