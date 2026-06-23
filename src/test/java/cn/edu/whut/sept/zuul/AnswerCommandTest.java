package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试答题命令：正确/错误回答、房间判断。
 */
public class AnswerCommandTest {
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
    public void testAnswerWithoutArgument() {
        AnswerCommand cmd = new AnswerCommand();
        cmd.setSecondWord(null);
        assertFalse("缺少参数应失败", cmd.execute(game));
    }

    @Test
    public void testCorrectAnswerInOffice() {
        // 移动到办公室
        Room office = game.findRoomByDescription("in the computing admin office");
        game.setCurrentRoom(office);

        AnswerCommand cmd = new AnswerCommand();
        cmd.setSecondWord("B"); // 管理员正确答案是 B
        int scoreBefore = game.getScore();
        int hpBefore = game.getHp();

        cmd.execute(game);

        assertTrue("答对应加分", game.getScore() > scoreBefore);
        assertTrue("答对应回血", game.getHp() >= hpBefore);
    }

    @Test
    public void testWrongAnswer() {
        Room office = game.findRoomByDescription("in the computing admin office");
        game.setCurrentRoom(office);

        AnswerCommand cmd = new AnswerCommand();
        cmd.setSecondWord("C");
        int hpBefore = game.getHp();

        cmd.execute(game);

        assertTrue("答错应扣血", game.getHp() < hpBefore);
    }

    @Test
    public void testAnswerInTheater() {
        Room theater = game.findRoomByDescription("in a lecture theater");
        game.setCurrentRoom(theater);

        AnswerCommand cmd = new AnswerCommand();
        cmd.setSecondWord("A"); // 老师正确答案是 A
        int scoreBefore = game.getScore();

        cmd.execute(game);

        assertTrue("老师在剧场答对应加分", game.getScore() > scoreBefore);
    }
}
