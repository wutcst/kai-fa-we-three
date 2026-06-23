package cn.edu.whut.sept.zuul;

/**
 * 游戏结局类型 —— 根据玩家表现决定通关评级。
 */
public enum EndingType {
    // 优先级从高到低排列（高的先判定，互不重叠）
    /** 全能：速通 + 全对 + 全收集 */
    GRAND_MASTER("全能学者", "30步内全对通关，校园每个角落都留下了你的传说。", 300),
    /** 所有答题一次答对（无逃跑、无答错） */
    PERFECT("学术之星", "所有挑战全对通过，导师对你刮目相看。", 200),
    /** 30 步内完成 */
    SPEEDRUN("闪电学者", "以惊人速度完成挑战，全校为之震惊！", 100),
    /** 收集 ≥10 件物品 + 走遍 ≥5 个房间 */
    EXPLORER("校园活地图", "校园的每个角落都留下了你的足迹。", 80),
    /** 全程不打架（至少逃跑过一次） */
    PACIFIST("佛系学生", "不动干戈，以智慧化解一切危机。", 50),
    /** 完成隐藏任务 */
    SECRET("时空旅者", "你在时空裂隙中找到了远古智慧，与完美报告一起提交，校园将永远铭记你的名字。", 500),
    /** 标准通关 */
    NORMAL("合格研究生", "完成了任务，校园系统恢复正常。", 0);

    private final String title;
    private final String description;
    private final int scoreBonus;

    EndingType(String title, String description, int scoreBonus) {
        this.title = title;
        this.description = description;
        this.scoreBonus = scoreBonus;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getScoreBonus() { return scoreBonus; }
}
