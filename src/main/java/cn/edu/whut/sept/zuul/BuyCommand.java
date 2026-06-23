package cn.edu.whut.sept.zuul;

/**
 * buy 命令 —— 从当前房间的商人处购买商品。
 */
public class BuyCommand extends Command {
    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            // 显示商店商品列表
            Room room = game.getCurrentRoom();
            Shop shop = game.getShopForRoom(room.getShortDescription());
            if (shop == null) {
                System.out.println("这个房间里没有商人。");
            } else {
                System.out.println("=== " + shop.getMerchantNpcName() + " 的商品 ===");
                for (ShopItem si : shop.getInventory()) {
                    System.out.println("  " + si.getName() + " - " + si.getPrice()
                            + " 金币 [" + si.getType() + "] " + si.getStats());
                }
                System.out.println("输入 buy <商品名> 购买。");
            }
            return false;
        }

        String itemName = getSecondWord();
        Room room = game.getCurrentRoom();

        // 检查当前房间是否有商人
        Shop shop = game.getShopForRoom(room.getShortDescription());
        if (shop == null) {
            System.out.println("这个房间里没有商人。");
            return false;
        }

        ShopItem shopItem = shop.findItem(itemName);
        if (shopItem == null) {
            System.out.println("这个商人没有出售 [" + itemName + "].");
            System.out.println("输入 buy 查看商品列表。");
            return false;
        }

        Player player = game.getPlayer();
        if (!player.spendGold(shopItem.getPrice())) {
            System.out.println("金币不足！需要 " + shopItem.getPrice()
                    + " 金币，你只有 " + player.getGold() + " 金币。");
            return false;
        }

        // 咖啡店消耗品：放到房间地上，玩家点击拾取
        if (itemName.equals("cookie") || itemName.equals("coffee")) {
            room.addItem(new Item(itemName, 1));
            System.out.println("你购买了 [" + itemName + "]，花费 " + shopItem.getPrice()
                    + " 金币。它已经出现在地上，点击拾取吧！剩余金币: " + player.getGold());
        } else {
            // 其他物品直接放入背包
            Item item = new Item(itemName, 1);
            if (!player.takeItem(item)) {
                player.addGold(shopItem.getPrice());
                System.out.println("背包已满，无法购买！");
                return false;
            }
            System.out.println("你购买了 [" + itemName + "]，花费 " + shopItem.getPrice()
                    + " 金币。剩余金币: " + player.getGold());
        }
        return false;
    }
}
