package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * 游戏核心流程测试：覆盖初始状态、移动、拾取、丢弃、背包、
 * 胜利条件、HP 和分数变更等核心逻辑。
 */
public class GameBasicTest {

    private Game game;
    private File tempDb;

    @Before
    public void setUp() {
        try {
            tempDb = File.createTempFile("zuul-basic-test-", ".db");
            tempDb.deleteOnExit();
            DatabaseManager dbManager = new DatabaseManager(tempDb.getAbsolutePath());
            dbManager.initialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to init test database", e);
        }
        game = new Game(dbManager);
    }

    @Test
    public void testInitialState() {
        assertEquals(GameConstants.INITIAL_HP, game.getHp());
        assertEquals(GameConstants.INITIAL_SCORE, game.getScore());
        assertFalse(game.isVictory());
        assertNotNull(game.getCurrentRoom());
        assertEquals(0, game.getPlayer().getCurrentWeight());
        assertEquals(GameConstants.DEFAULT_PLAYER_NAME, game.getPlayer().getName());
    }

    @Test
    public void testMoveToValidRoom() {
        Room start = game.getCurrentRoom();
        // outside east → theater
        Room theater = start.getExit("east");
        assertNotNull("east exit should exist", theater);
        game.moveToRoom(theater);
        assertNotEquals(start.getShortDescription(),
                game.getCurrentRoom().getShortDescription());
    }

    @Test
    public void testBackCommand() {
        Room first = game.getCurrentRoom();
        game.moveToRoom(first.getExit("east"));
        Room popped = game.popLastRoom();
        assertNotNull(popped);
        assertEquals(first.getShortDescription(), popped.getShortDescription());
    }

    @Test
    public void testTakeItem() {
        Room outside = game.getCurrentRoom();
        int initialCount = outside.getItems().size();
        assertTrue("outside should have items", initialCount >= 1);

        Item item = outside.getItems().get(0);
        boolean taken = game.getPlayer().takeItem(item);
        assertTrue("should be able to take item within weight limit", taken);
        assertTrue(game.getPlayer().getCurrentWeight() > 0);
    }

    @Test
    public void testDropItem() {
        Player player = game.getPlayer();
        Item item = new Item("test_item", 2);
        player.takeItem(item);
        assertEquals(2, player.getCurrentWeight());

        Item dropped = player.dropItem("test_item");
        assertNotNull(dropped);
        assertEquals(0, player.getCurrentWeight());
    }

    @Test
    public void testDropNonExistentItem() {
        Player player = game.getPlayer();
        Item dropped = player.dropItem("nonexistent");
        assertNull(dropped);
    }

    @Test
    public void testInventoryNotEmptyAfterTake() {
        Player player = game.getPlayer();
        player.takeItem(new Item("map", 1));
        assertFalse(player.getInventoryItems().isEmpty());
    }

    @Test
    public void testOverweightPreventsTake() {
        Player player = game.getPlayer();
        // 玩家默认 maxWeight=10，放一个 15 的物品应该失败
        Item heavy = new Item("heavy_rock", 15);
        assertFalse("should fail due to overweight", player.takeItem(heavy));
        assertEquals(0, player.getCurrentWeight());
    }

    @Test
    public void testVictoryCondition() {
        assertFalse(game.checkVictory());
        Room start = game.findRoomByDescription(
                "outside the main entrance of the university");
        assertNotNull(start);
        start.addItem(new Item("task_item", 2));
        assertTrue(game.checkVictory());
        assertTrue(game.isVictory());
    }

    @Test
    public void testHpChanges() {
        int initialHp = game.getHp();
        game.setHp(game.getHp() - GameConstants.HP_LOSS_WALL);
        assertEquals(initialHp - GameConstants.HP_LOSS_WALL, game.getHp());
        game.setHp(game.getHp() + GameConstants.HP_GAIN_TALK);
        assertEquals(initialHp - GameConstants.HP_LOSS_WALL
                + GameConstants.HP_GAIN_TALK, game.getHp());
    }

    @Test
    public void testScoreChanges() {
        game.addScore(10);
        assertEquals(10, game.getScore());
        game.addScore(5);
        assertEquals(15, game.getScore());
    }

    @Test
    public void testQuestProgressInitialized() {
        assertEquals(GameConstants.QUEST_STARTED,
                game.getQuestProgressValue(GameConstants.QUEST_MAIN));
        assertEquals(GameConstants.QUEST_NOT_STARTED,
                game.getQuestProgressValue(GameConstants.QUEST_SIDE_COOKIE));
    }

    @Test
    public void testResetWorld() {
        game.setHp(50);
        game.setScore(30);
        game.resetWorld();
        assertEquals(GameConstants.INITIAL_HP, game.getHp());
        assertEquals(GameConstants.INITIAL_SCORE, game.getScore());
        assertFalse(game.isVictory());
        assertEquals(0, game.getPlayer().getCurrentWeight());
    }
}
