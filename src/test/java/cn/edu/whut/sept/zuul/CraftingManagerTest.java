package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试合成/物品组合系统。
 */
public class CraftingManagerTest {
    private CraftingManager craftingManager;
    private Game game;
    private DatabaseManager db;

    @Before
    public void setUp() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("zuul-test-", ".db");
        tmp.deleteOnExit();
        db = new DatabaseManager(tmp.getAbsolutePath());
        db.initialize();
        game = new Game(db);
        craftingManager = new CraftingManager();
    }

    @Test
    public void testCraftWithAllIngredients() {
        // 给玩家三个材料
        game.getPlayer().takeItem(new Item("code_data", 1));
        game.getPlayer().takeItem(new Item("reference", 1));
        game.getPlayer().takeItem(new Item("signature", 1));

        String result = craftingManager.findCraftable(game.getPlayer());
        assertEquals("三材料齐全应可合成 perfect_report", "perfect_report", result);
    }

    @Test
    public void testCraftWithMissingIngredient() {
        game.getPlayer().takeItem(new Item("code_data", 1));
        game.getPlayer().takeItem(new Item("reference", 1));
        // 缺少 signature

        String result = craftingManager.findCraftable(game.getPlayer());
        assertNull("缺少材料应返回 null", result);
    }

    @Test
    public void testCraftWithNoItems() {
        String result = craftingManager.findCraftable(game.getPlayer());
        assertNull("空背包应返回 null", result);
    }

    @Test
    public void testGetDescription() {
        String desc = craftingManager.getDescription("perfect_report");
        assertNotNull("应有描述", desc);
        assertFalse("描述不应为空", desc.isEmpty());
    }

    @Test
    public void testUnknownRecipe() {
        String desc = craftingManager.getDescription("nonexistent");
        assertEquals("未知配方应返回空", "", desc);
    }
}
