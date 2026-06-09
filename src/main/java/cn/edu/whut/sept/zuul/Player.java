package cn.edu.whut.sept.zuul;

public class Player {
    private String name;
    private Room currentRoom;
    private int maxWeight; // 最大负重

    public Player(String name, int maxWeight) {
        this.name = name;
        this.maxWeight = maxWeight;
    }

    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public Room getCurrentRoom() { return currentRoom; }

    // 预留给成员A的接口
    public boolean takeItem(Item item) { return false; }
    public Item dropItem(String itemName) { return null; }
}