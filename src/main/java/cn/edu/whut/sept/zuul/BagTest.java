package cn.edu.whut.sept.zuul;

public class BagTest {
    public static void main(String[] args) {
        // 1. 初始化一个玩家，限重 10kg
        Player player = new Player("张三", 10);
        System.out.println("====== 初始状态 ======");
        System.out.println(player.getInventoryString());

        // 2. 创造三个测试物品
        Item sword = new Item("sword", 5);  // 剑：5kg
        Item shield = new Item("shield", 4); // 盾：4kg
        Item stone = new Item("stone", 3);   // 石头：3kg (捡起这个就会超重)

        // 3. 测试拾取逻辑与超重拦截
        System.out.println("\n====== 开始捡拾物品 ======");
        System.out.println("捡起 sword (5kg): " + player.takeItem(sword));   // 预期：true
        System.out.println("捡起 shield (4kg): " + player.takeItem(shield));  // 预期：true
        System.out.println("试图捡起 stone (3kg): " + player.takeItem(stone));  // 预期：false (因为 5+4+3=12 > 10)

        // 4. 打印当前背包状态
        System.out.println("\n====== 检查当前背包 ======");
        System.out.println(player.getInventoryString());

        // 5. 测试丢弃逻辑
        System.out.println("\n====== 开始丢弃物品 ======");
        Item dropped = player.dropItem("sword");
        if(dropped != null) {
            System.out.println("成功扔掉了: " + dropped.getDescription());
        }

        // 6. 最终背包状态
        System.out.println("\n====== 最终背包状态 ======");
        System.out.println(player.getInventoryString());
    }
}