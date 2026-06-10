package cn.edu.whut.sept.zuul;

public class Item {
    private String description; // 物品名称/描述 (如 "sword" 或 "magic cookie")
    private int weight;         // 物品重量

    public Item(String description, int weight) {
        this.description = description;
        this.weight = weight;
    }

    public String getDescription() { return description; }
    public int getWeight() { return weight; }
}