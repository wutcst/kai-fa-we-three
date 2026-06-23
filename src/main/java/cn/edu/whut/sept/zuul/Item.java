package cn.edu.whut.sept.zuul;

public class Item {
    private String description; // 物品的描述/名称
    private int weight;         // 物品的重量 (负重拦截的核心依赖)
    private String lockedByNpc; // 如果不为null，说明该物品关联的NPC
    private boolean unlocked;    // NPC 答题后解锁，但 lockedByNpc 保留用于重试拾取

    public Item(String description, int weight) {
        this(description, weight, null);
    }

    public Item(String description, int weight, String lockedByNpc) {
        this.description = description;
        this.weight = weight;
        this.lockedByNpc = lockedByNpc;
        this.unlocked = false;
    }

    public String getDescription() {
        return description;
    }

    public int getWeight() {
        return weight;
    }

    public String getLockedByNpc() {
        return lockedByNpc;
    }

    /** 物品是否仍处于锁定状态（未答题解锁） */
    public boolean isLocked() {
        return lockedByNpc != null && !unlocked;
    }

    public void unlock() {
        this.unlocked = true;
    }
}