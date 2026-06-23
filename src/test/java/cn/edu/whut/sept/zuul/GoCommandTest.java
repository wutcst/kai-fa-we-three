package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试移动命令：正常移动、撞墙、传送室。
 */
public class GoCommandTest {
    private Game game;
    private DatabaseManager db;

    @Before
    public void setUp() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("zuul-test-", ".db");
        tmp.deleteOnExit();
        db = new DatabaseManager(tmp.getAbsolutePath());
        db.initialize();
        game = new Game(db);
    }

    @Test
    public void testGoWithoutDirection() {
        GoCommand cmd = new GoCommand();
        cmd.setSecondWord(null);
        assertFalse("缺少方向应失败", cmd.execute(game));
    }

    @Test
    public void testGoToValidExit() {
        // 从外面往东去教学楼（需要先拿地图才能出正门）
        Room outside = game.findRoomByDescription("outside the main entrance of the university");
        game.setCurrentRoom(outside);
        game.getPlayer().takeItem(new Item("campus_map", 1));

        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("east");
        int hpBefore = game.getHp();

        cmd.execute(game);

        assertTrue("移动成功应回血", game.getHp() > hpBefore);
        assertTrue("应移动到教学楼", game.getCurrentRoom().getShortDescription().contains("lecture theater"));
    }

    @Test
    public void testGoToInvalidExit_hitsWall() {
        Room outside = game.findRoomByDescription("outside the main entrance of the university");
        game.setCurrentRoom(outside);

        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("north"); // 外面没有北出口
        int hpBefore = game.getHp();

        cmd.execute(game);

        assertEquals("撞墙应扣2HP", hpBefore - 2, game.getHp());
    }

    @Test
    public void testTeleportRoomBlockedAtLowLevel() {
        // Lv.1 不能进传送室
        Room theater = game.findRoomByDescription("in a lecture theater");
        game.setCurrentRoom(theater);
        assertEquals("初始等级应为1", 1, game.getPlayer().getLevel());

        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("north"); // 教学楼北是传送室
        int hpBefore = game.getHp();

        cmd.execute(game);

        // HP不应变化（被门禁拦截，不算撞墙）
        assertEquals("低等级应被拦截", hpBefore, game.getHp());
    }

    @Test
    public void testBackCommand() {
        Room outside = game.findRoomByDescription("outside the main entrance of the university");
        game.setCurrentRoom(outside);

        GoCommand go = new GoCommand();
        go.setSecondWord("east");
        go.execute(game);

        // 现在在教学楼
        BackCommand back = new BackCommand();
        back.execute(game);

        assertTrue("返回应回到外面", game.getCurrentRoom().getShortDescription().contains("outside"));
    }
}
