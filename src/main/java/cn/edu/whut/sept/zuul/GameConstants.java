package cn.edu.whut.sept.zuul;

/**
 * 游戏全局常量定义，统一管理所有魔法数字和任务状态字符串。
 */
public final class GameConstants {
    private GameConstants() {
    }

    // ========== 生命值 ==========
    public static final int INITIAL_HP = 10;
    public static final int HP_GAIN_MOVE = 1;
    public static final int HP_LOSS_WALL = 2;
    public static final int HP_GAIN_TALK = 5;
    public static final int HP_LOSS_TALK_FAIL = 2;
    public static final int HP_GAIN_TAKE = 3;
    public static final int HP_LOSS_OVERWEIGHT = 3;
    public static final int HP_LOSS_EAT_FAIL = 3;
    public static final int HP_GAIN_EAT_COOKIE = 20;
    public static final int HP_GAIN_EAT_OTHER = 5;

    // ========== 分数 ==========
    public static final int INITIAL_SCORE = 0;
    public static final int SCORE_GAIN_TALK = 3;
    public static final int SCORE_GAIN_COOKIE = 15;

    // ========== 负重 ==========
    public static final double DEFAULT_MAX_WEIGHT = 10.0;
    public static final int COOKIE_WEIGHT_BONUS = 10;

    // ========== 任务键 ==========
    public static final String QUEST_MAIN = "main_quest";
    public static final String QUEST_SIDE_COOKIE = "side_quest_cookie";

    // ========== 任务状态 ==========
    public static final String QUEST_STARTED = "started";
    public static final String QUEST_NOT_STARTED = "not_started";
    public static final String QUEST_COMPLETED = "completed";
    public static final String QUEST_HINT_RECEIVED = "hint_received";
    public static final String QUEST_COLLECTED_TASK_ITEM = "collected_task_item";

    // ========== 玩家默认值 ==========
    public static final String DEFAULT_PLAYER_NAME = "Adventurer";

    // ========== 金币 ==========
    public static final int INITIAL_GOLD = 0;

    // ========== 战斗与属性 ==========
    public static final int BASE_ATK = 10;
    public static final int BASE_DEF = 5;
    public static final int BASE_SP = 50;
    public static final int BASE_LEVEL = 1;
    public static final int BASE_EXP = 0;

    // ========== 经验与升级 ==========
    /** 每级所需经验值 (索引 = 等级-1)，0 表示已满级 */
    public static final int[] EXP_THRESHOLDS = {
        0, 60, 150, 300, 500
    };
    public static final int ATK_PER_LEVEL = 2;
    public static final int DEF_PER_LEVEL = 1;
    public static final int SP_PER_LEVEL = 5;
    public static final int HP_PER_LEVEL = 10;
    public static final int MAX_LEVEL = 5;

    // ========== 战斗伤害公式常量 ==========
    public static final double DAMAGE_VARIANCE = 0.2;    // 伤害随机波动 ±20%
    public static final double DEF_REDUCTION_RATE = 0.5; // 防御减伤系数
    public static final double DEFEND_REDUCTION = 0.5;   // 防御姿态额外减伤
    public static final int FLEE_HP_PENALTY = 5;         // 逃跑惩罚扣血
}
