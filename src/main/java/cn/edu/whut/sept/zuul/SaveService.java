package cn.edu.whut.sept.zuul;

import java.sql.SQLException;
import java.util.List;

/**
 * 存档业务服务层，负责在游戏与数据库之间转换存档数据。
 */
public class SaveService
{
    private final SaveRepository saveRepository;
    private final LeaderboardRepository leaderboardRepository;

    public SaveService(SaveRepository saveRepository,
                       LeaderboardRepository leaderboardRepository)
    {
        this.saveRepository = saveRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    /**
     * 获取排行榜前 N 名。
     */
    public List<LeaderboardEntry> getLeaderboard(int limit) throws SQLException
    {
        return leaderboardRepository.getTopScores(limit);
    }

    public String save(Game game, String saveName) throws SaveException
    {
        validateLoggedIn(game);
        String normalizedSaveName = normalizeSaveName(saveName);

        try {
            long saveId = saveRepository.saveGame(
                    game.getLoggedInProfile().getId(),
                    normalizedSaveName,
                    game.createSnapshot()
            );
            // Sync leaderboard when saving.
            try {
                String endingTitle = game.getEndingType() != null
                        ? game.getPlayer().getLevelTitle() + " · " + game.getEndingType().getTitle()
                        : "";
                leaderboardRepository.updateEntry(
                        game.getLoggedInProfile().getId(),
                        game.getLoggedInProfile().getName(),
                        game.getScore(),
                        game.getHp(),
                        game.isVictory(),
                        endingTitle,
                        java.time.Instant.now().toString()
                );
            } catch (SQLException e) {
                // Leaderboard update should not block saving.
                System.err.println("Leaderboard update failed: " + e.getMessage());
            }
            return "存档 \"" + normalizedSaveName + "\" 保存成功（ID: " + saveId + "）。";
        } catch (SQLException e) {
            throw new SaveException("保存失败，请稍后重试。");
        }
    }

    public String load(Game game, String saveName) throws SaveException
    {
        validateLoggedIn(game);
        String normalizedSaveName = normalizeSaveName(saveName);

        try {
            GameSaveRecord saveRecord = saveRepository.loadGame(
                    game.getLoggedInProfile().getId(),
                    normalizedSaveName
            );
            if (saveRecord == null) {
                throw new SaveException("未找到存档 \"" + normalizedSaveName + "\"。");
            }

            game.applySnapshot(saveRecord);
            return "存档 \"" + normalizedSaveName + "\" 读取成功。\n"
                    + "当前房间：" + saveRecord.getCurrentRoomName() + "\n"
                    + "生命值：" + saveRecord.getHealth()
                    + "，分数：" + saveRecord.getScore() + "\n"
                    + "负重：" + saveRecord.getCurrentWeight()
                    + " / " + saveRecord.getMaxWeight() + "\n"
                    + "任务进度：" + saveRecord.getQuestProgress().getOrDefault("main_quest", "unknown") + "\n"
                    + game.getPlayer().getInventoryString();
        } catch (SQLException e) {
            throw new SaveException("读档失败，请稍后重试。");
        }
    }

    public String listSaves(Game game) throws SaveException
    {
        validateLoggedIn(game);

        try {
            List<GameSaveRecord> saves = saveRepository.listSaves(game.getLoggedInProfile().getId());
            if (saves.isEmpty()) {
                return "当前没有可用存档。";
            }

            StringBuilder builder = new StringBuilder("你的存档列表：");
            for (GameSaveRecord save : saves) {
                builder.append("\n - ").append(save.getSaveName())
                        .append(" | 房间: ").append(save.getCurrentRoomName())
                        .append(" | HP: ").append(save.getHealth())
                        .append(" | 分数: ").append(save.getScore())
                        .append(" | 保存时间: ").append(save.getSavedAt());
            }
            return builder.toString();
        } catch (SQLException e) {
            throw new SaveException("读取存档列表失败，请稍后重试。");
        }
    }

    public String deleteSave(Game game, String saveName) throws SaveException
    {
        validateLoggedIn(game);
        String normalizedSaveName = normalizeSaveName(saveName);

        try {
            boolean deleted = saveRepository.deleteSave(
                    game.getLoggedInProfile().getId(),
                    normalizedSaveName
            );
            if (!deleted) {
                throw new SaveException("未找到存档 \"" + normalizedSaveName + "\"，无法删除。");
            }
            return "存档 \"" + normalizedSaveName + "\" 已删除。";
        } catch (SQLException e) {
            throw new SaveException("删除存档失败，请稍后重试。");
        }
    }

    /**
     * 将当前玩家成绩加入排行榜，无需完整存档。
     */
    public void joinLeaderboard(Game game) throws SaveException
    {
        validateLoggedIn(game);
        try {
            String endingTitle = game.getEndingType() != null
                    ? game.getPlayer().getLevelTitle() + " · " + game.getEndingType().getTitle()
                    : "";
            leaderboardRepository.updateEntry(
                    game.getLoggedInProfile().getId(),
                    game.getLoggedInProfile().getName(),
                    game.getScore(),
                    game.getHp(),
                    game.isVictory(),
                    endingTitle,
                    java.time.Instant.now().toString()
            );
        } catch (SQLException e) {
            throw new SaveException("加入排行榜失败，请稍后重试。");
        }
    }

    private void validateLoggedIn(Game game) throws SaveException
    {
        if (!game.isLoggedIn()) {
            throw new SaveException("请先使用 login <username> 登录后再操作存档。");
        }
    }

    private String normalizeSaveName(String saveName) throws SaveException
    {
        if (saveName == null || saveName.trim().isEmpty()) {
            throw new SaveException("存档名称不能为空。");
        }
        return saveName.trim();
    }
}
