package cn.edu.whut.sept.zuul;

/**
 * 根据玩家统计数据计算通关结局和评分。
 */
public class EndingCalculator {

    /**
     * 按优先级从高到低判定结局，互不重叠。
     * 优先级：全能 > 学霸 > 速通 > 探索者 > 和平 > 普通
     */
    public static EndingType calculate(Game game, PlayerStats stats) {
        boolean perfect = stats.quizTotal > 0 && stats.quizCorrect == stats.quizTotal && stats.fleeCount == 0;
        boolean speed = stats.stepCount <= 30;
        boolean explorer = stats.itemsCollected >= 10 && stats.roomsVisited >= 5;
        // 隐藏任务：进入传送室3次以上 或 与神谕者对话3次以上
        boolean secret = game.getTeleportVisits() >= 3 || game.getNpcAffinity("oracle") >= 3;

        // 全能学者：同时满足学霸 + 速通 + 探索者
        if (perfect && speed && explorer) {
            return EndingType.GRAND_MASTER;
        }
        // 时空旅者：完成神谕者隐藏任务
        if (secret) {
            return EndingType.SECRET;
        }
        // 学术之星：全对且无逃跑
        if (perfect) {
            return EndingType.PERFECT;
        }
        // 闪电学者：30步内
        if (speed) {
            return EndingType.SPEEDRUN;
        }
        // 校园活地图：全收集+全探索
        if (explorer) {
            return EndingType.EXPLORER;
        }
        return EndingType.NORMAL;
    }

    public static int calculateScore(Game game, PlayerStats stats, EndingType ending) {
        return game.getScore() + ending.getScoreBonus();
    }

    /** 玩家统计数据 */
    public static class PlayerStats {
        public int stepCount = 0;
        public int quizTotal = 0;
        public int quizCorrect = 0;
        public int enemiesDefeated = 0;
        public int fleeCount = 0;
        public int itemsCollected = 0;
        public int roomsVisited = 0;
    }
}
