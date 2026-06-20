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
     * 更新排行榜：插入或更新该玩家的最高分记录。
     */
    public void updateEntry(long playerId, String playerName, int score,
                            int health, boolean isVictory, String savedAt)
            throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO leaderboard (player_id, player_name, score, "
                             + "health, is_victory, saved_at) "
                             + "VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setLong(1, playerId);
            statement.setString(2, playerName);
            statement.setInt(3, score);
            statement.setInt(4, health);
            statement.setInt(5, isVictory ? 1 : 0);
            statement.setString(6, savedAt);
            statement.executeUpdate();
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
                     "SELECT player_name, MAX(score) AS score, health, "
                             + "is_victory, saved_at "
                             + "FROM leaderboard "
                             + "GROUP BY player_name "
                             + "ORDER BY score DESC "
                             + "LIMIT " + limit)) {
            while (rs.next()) {
                entries.add(new LeaderboardEntry(
                        rs.getString("player_name"),
                        rs.getInt("score"),
                        rs.getInt("health"),
                        rs.getInt("is_victory") == 1,
                        rs.getString("saved_at")
                ));
            }
        }
        return entries;
    }
}
