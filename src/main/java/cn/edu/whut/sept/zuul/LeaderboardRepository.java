package cn.edu.whut.sept.zuul;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责 leaderboard 表的读写。
 */
public class LeaderboardRepository {

    private final DatabaseManager databaseManager;

    public LeaderboardRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * 更新排行榜：先删旧记录再插入，确保每人只有最高分一条记录。
     */
    public void updateEntry(long playerId, String playerName, int score,
                            int health, boolean isVictory, String endingTitle,
                            String savedAt)
            throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            // 删除该玩家旧记录（确保只保留最新/最高分）
            try (PreparedStatement del = connection.prepareStatement(
                    "DELETE FROM leaderboard WHERE player_id = ? AND score <= ?")) {
                del.setLong(1, playerId);
                del.setInt(2, score);
                del.executeUpdate();
            }
            // 插入新记录
            try (PreparedStatement ins = connection.prepareStatement(
                    "INSERT INTO leaderboard (player_id, player_name, score, "
                            + "health, is_victory, ending_title, saved_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ins.setLong(1, playerId);
                ins.setString(2, playerName);
                ins.setInt(3, score);
                ins.setInt(4, health);
                ins.setInt(5, isVictory ? 1 : 0);
                ins.setString(6, endingTitle);
                ins.setString(7, savedAt);
                ins.executeUpdate();
            }
        }
    }

    /**
     * 获取排行榜，按分数降序排列，返回前 20 条。
     */
    public List<LeaderboardEntry> getTopScores(int limit) throws SQLException {
        List<LeaderboardEntry> entries = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT player_name, score, health, "
                             + "is_victory, ending_title, saved_at "
                             + "FROM leaderboard "
                             + "ORDER BY score DESC "
                             + "LIMIT " + limit)) {
            while (rs.next()) {
                entries.add(new LeaderboardEntry(
                        rs.getString("player_name"),
                        rs.getInt("score"),
                        rs.getInt("health"),
                        rs.getInt("is_victory") == 1,
                        rs.getString("ending_title"),
                        rs.getString("saved_at")
                ));
            }
        }
        return entries;
    }
}
