package cn.edu.whut.sept.zuul;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试结局计算系统：不同表现对应不同结局。
 */
public class EndingCalculatorTest {
    private Game game;
    private DatabaseManager db;
    private EndingCalculator.PlayerStats stats;

    @Before
    public void setUp() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("zuul-test-", ".db");
        tmp.deleteOnExit();
        db = new DatabaseManager(tmp.getAbsolutePath());
        db.initialize();
        game = new Game(db);
        stats = new EndingCalculator.PlayerStats();
    }

    @Test
    public void testNormalEnding() {
        stats.stepCount = 100;
        stats.quizTotal = 1;
        stats.quizCorrect = 0;
        stats.itemsCollected = 2;
        stats.roomsVisited = 2;
        stats.fleeCount = 0;

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("普通表现应为普通结局", EndingType.NORMAL, ending);
    }

    @Test
    public void testPerfectEnding() {
        stats.stepCount = 100;
        stats.quizTotal = 2;
        stats.quizCorrect = 2;  // 全对
        stats.itemsCollected = 5;
        stats.roomsVisited = 3;
        stats.fleeCount = 0;     // 无逃跑

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("答题全对应为学术之星", EndingType.PERFECT, ending);
    }

    @Test
    public void testSpeedrunEnding() {
        stats.stepCount = 25;   // ≤30
        stats.quizTotal = 1;
        stats.quizCorrect = 0;
        stats.itemsCollected = 3;
        stats.roomsVisited = 2;
        stats.fleeCount = 0;

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("30步内应为闪电学者", EndingType.SPEEDRUN, ending);
    }

    @Test
    public void testExplorerEnding() {
        stats.stepCount = 100;
        stats.quizTotal = 1;
        stats.quizCorrect = 0;
        stats.itemsCollected = 12;   // ≥10
        stats.roomsVisited = 6;      // ≥5
        stats.fleeCount = 0;

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("全收集全探索应为校园活地图", EndingType.EXPLORER, ending);
    }

    @Test
    public void testGrandMasterEnding() {
        stats.stepCount = 25;        // ≤30
        stats.quizTotal = 2;
        stats.quizCorrect = 2;       // 全对
        stats.itemsCollected = 12;   // ≥10
        stats.roomsVisited = 6;      // ≥5
        stats.fleeCount = 0;         // 无逃跑

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("全部满足应为全能学者", EndingType.GRAND_MASTER, ending);
    }

    @Test
    public void testSecretEnding() {
        // 模拟3次传送室访问
        game.incrementTeleportVisits();
        game.incrementTeleportVisits();
        game.incrementTeleportVisits();

        stats.stepCount = 100;
        stats.quizTotal = 1;
        stats.quizCorrect = 0;
        stats.itemsCollected = 5;
        stats.roomsVisited = 3;
        stats.fleeCount = 0;

        EndingType ending = EndingCalculator.calculate(game, stats);
        assertEquals("传送3次应为时空旅者", EndingType.SECRET, ending);
    }

    @Test
    public void testScoreCalculation() {
        game.addScore(200);
        int finalScore = EndingCalculator.calculateScore(game, stats, EndingType.PERFECT);
        assertEquals("评分 = 基础分 + 结局加成", 200 + 200, finalScore);
    }

    @Test
    public void testLevelTitle() {
        assertEquals("Lv.1", "📚 新生", game.getPlayer().getLevelTitle());
    }
}
