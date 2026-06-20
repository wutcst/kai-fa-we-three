package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * 命令单元测试：覆盖 Go / Eat / Take / Drop / Talk / Back 等命令的
 * 正常路径和异常路径。
 */
public class CommandTest {

    private Game game;

    @Before
    public void setUp() {
        try {
            File tempDb = File.createTempFile("zuul-cmd-test-", ".db");
            tempDb.deleteOnExit();
            DatabaseManager dbManager = new DatabaseManager(tempDb.getAbsolutePath());
            dbManager.initialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to init test database", e);
        }
        game = new Game(dbManager);
    }

    // ======================== GoCommand ========================

    @Test
    public void testGoValidDirection() {
        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("east");
        cmd.execute(game);
        // outside east → theater
        assertTrue(game.getCurrentRoom().getShortDescription()
                .contains("lecture theater"));
    }

    @Test
    public void testGoInvalidDirection() {
        int hpBefore = game.getHp();
        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("up");
        cmd.execute(game);
        assertEquals(hpBefore - GameConstants.HP_LOSS_WALL, game.getHp());
    }

    @Test
    public void testGoWithoutDirection() {
        GoCommand cmd = new GoCommand();
        // 没有 setSecondWord
        cmd.execute(game);
        // 不应该报错（只是提示"Go where?"）
    }

    // ======================== EatCommand ========================

    @Test
    public void testEatCookie() {
        Player player = game.getPlayer();
        player.takeItem(new Item("cookie", 1));
        int maxBefore = player.getMaxWeight();

        EatCommand cmd = new EatCommand();
        cmd.setSecondWord("cookie");
        cmd.execute(game);

        assertEquals(maxBefore + GameConstants.COOKIE_WEIGHT_BONUS,
                player.getMaxWeight());
        // cookie 应该从背包消失
        assertNull(player.dropItem("cookie"));
    }

    @Test
    public void testEatCookieNotInInventory() {
        int hpBefore = game.getHp();
        EatCommand cmd = new EatCommand();
        cmd.setSecondWord("cookie");
        cmd.execute(game);
        // cookie 不在背包 → 失败扣血
        assertEquals(hpBefore - GameConstants.HP_LOSS_EAT_FAIL, game.getHp());
    }

    @Test
    public void testEatNonEdibleItem() {
        Player player = game.getPlayer();
        player.takeItem(new Item("campus_map", 1));
        int hpBefore = game.getHp();

        EatCommand cmd = new EatCommand();
        cmd.setSecondWord("campus_map");
        cmd.execute(game);

        assertEquals(hpBefore - GameConstants.HP_LOSS_EAT_FAIL, game.getHp());
        // 物品应该还在背包里
        assertNotNull(player.dropItem("campus_map"));
    }

    // ======================== TakeCommand ========================

    @Test
    public void testTakeExistingItem() {
        Room outside = game.getCurrentRoom();
        outside.addItem(new Item("test_key", 2));
        int initialWeight = game.getPlayer().getCurrentWeight();

        TakeCommand cmd = new TakeCommand();
        cmd.setSecondWord("test_key");
        cmd.execute(game);

        assertTrue(game.getPlayer().getCurrentWeight() > initialWeight);
    }

    @Test
    public void testTakeNonExistentItem() {
        TakeCommand cmd = new TakeCommand();
        cmd.setSecondWord("ghost_item");
        cmd.execute(game);
        // 不应崩溃
    }

    // ======================== DropCommand ========================

    @Test
    public void testDropExistingItem() {
        Player player = game.getPlayer();
        player.takeItem(new Item("test_rock", 3));
        assertEquals(3, player.getCurrentWeight());

        DropCommand cmd = new DropCommand();
        cmd.setSecondWord("test_rock");
        cmd.execute(game);

        assertEquals(0, player.getCurrentWeight());
    }

    @Test
    public void testDropNonExistentItem() {
        DropCommand cmd = new DropCommand();
        cmd.setSecondWord("nothing");
        cmd.execute(game);
        // 不应崩溃
    }

    // ======================== TalkCommand ========================

    @Test
    public void testTalkToExistingNPC() {
        int hpBefore = game.getHp();
        TalkCommand cmd = new TalkCommand();
        cmd.setSecondWord("guard");
        cmd.execute(game);
        assertEquals(hpBefore + GameConstants.HP_GAIN_TALK, game.getHp());
    }

    @Test
    public void testTalkToNonExistentNPC() {
        int hpBefore = game.getHp();
        TalkCommand cmd = new TalkCommand();
        cmd.setSecondWord("ghost");
        cmd.execute(game);
        assertEquals(hpBefore - GameConstants.HP_LOSS_TALK_FAIL, game.getHp());
    }

    // ======================== BackCommand ========================

    @Test
    public void testBackAfterMove() {
        String firstRoom = game.getCurrentRoom().getShortDescription();
        game.moveToRoom(game.getCurrentRoom().getExit("east"));
        assertNotEquals(firstRoom,
                game.getCurrentRoom().getShortDescription());

        BackCommand cmd = new BackCommand();
        cmd.execute(game);
        assertEquals(firstRoom,
                game.getCurrentRoom().getShortDescription());
    }

    // ======================== LookCommand ========================

    @Test
    public void testLookCommand() {
        LookCommand cmd = new LookCommand();
        cmd.execute(game);
        // 不应崩溃，且当前房间不变
        assertNotNull(game.getCurrentRoom());
    }

    // ======================== HelpCommand ========================

    @Test
    public void testHelpCommand() {
        HelpCommand cmd = new HelpCommand();
        cmd.execute(game);
        // 不应崩溃
    }
}
