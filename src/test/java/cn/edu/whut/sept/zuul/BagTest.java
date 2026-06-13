package cn.edu.whut.sept.zuul;

import org.junit.Test;

import static org.junit.Assert.*;

public class BagTest {

    @Test
    public void testTakeItemWithinWeightLimit() {
        Player player = new Player("张三", 10);
        Item sword = new Item("sword", 5);
        Item shield = new Item("shield", 4);

        assertTrue(player.takeItem(sword));
        assertTrue(player.takeItem(shield));
    }

    @Test
    public void testTakeItemExceedWeightLimit() {
        Player player = new Player("张三", 10);
        Item sword = new Item("sword", 5);
        Item shield = new Item("shield", 4);
        Item stone = new Item("stone", 3);

        assertTrue(player.takeItem(sword));
        assertTrue(player.takeItem(shield));
        assertFalse(player.takeItem(stone));
    }

    @Test
    public void testDropItem() {
        Player player = new Player("张三", 10);
        Item sword = new Item("sword", 5);

        player.takeItem(sword);
        Item dropped = player.dropItem("sword");

        assertNotNull(dropped);
        assertEquals("sword", dropped.getDescription());
    }

    @Test
    public void testEatMagicCookie() {
        Player player = new Player("张三", 10);
        Item cookie = new Item("cookie", 1);

        player.takeItem(cookie);
        boolean result = player.eat("cookie");

        assertTrue(result);
    }
}