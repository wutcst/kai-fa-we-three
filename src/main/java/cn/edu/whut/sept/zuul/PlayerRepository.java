package cn.edu.whut.sept.zuul;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

/**
 * 负责 player 表的数据访问：创建玩家、按用户名查询、登录逻辑。
 */
public class PlayerRepository
{
    private static final double DEFAULT_MAX_WEIGHT = GameConstants.DEFAULT_MAX_WEIGHT;
    private final DatabaseManager databaseManager;

    public PlayerRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    /**
     * 创建新玩家档案。
     *
     * @return 新创建的玩家记录
     * @throws SQLException 数据库异常
     * @throws IllegalArgumentException 用户名为空
     */
    public PlayerRecord createPlayer(String name, String password) throws SQLException
    {
        String normalizedName = normalizeName(name);
        if (normalizedName == null) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        if (findByName(normalizedName) != null) {
            throw new DuplicatePlayerException(normalizedName);
        }

        String now = Instant.now().toString();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO player (name, max_weight, password, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, normalizedName);
            statement.setDouble(2, DEFAULT_MAX_WEIGHT);
            statement.setString(3, password != null ? password : "");
            statement.setString(4, now);
            statement.setString(5, now);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new PlayerRecord(id, normalizedName, DEFAULT_MAX_WEIGHT, password != null ? password : "", now, now);
                }
            }
        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new DuplicatePlayerException(normalizedName, e);
            }
            throw e;
        }

        throw new SQLException("Failed to create player: " + normalizedName);
    }

    /**
     * 根据用户名查询玩家。
     *
     * @return 找到则返回玩家记录，否则返回 null
     */
    public PlayerRecord findByName(String name) throws SQLException
    {
        String normalizedName = normalizeName(name);
        if (normalizedName == null) {
            return null;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, max_weight, password, created_at, updated_at "
                             + "FROM player WHERE name = ?")) {
            statement.setString(1, normalizedName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPlayerRecord(resultSet);
                }
            }
        }
        return null;
    }

    /**
     * 登录逻辑：已有玩家直接登录，新玩家自动创建档案。
     */
    public PlayerLoginResult login(String name, String password) throws SQLException
    {
        String normalizedName = normalizeName(name);
        if (normalizedName == null) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        PlayerRecord existingPlayer = findByName(normalizedName);
        if (existingPlayer == null) {
            throw new IllegalArgumentException("该用户不存在，请先注册。");
        }
        // 已有用户，必须验证密码
        String storedPassword = existingPlayer.getPassword();
        if (storedPassword == null || storedPassword.isEmpty()) {
            throw new IllegalArgumentException("该账号未设置密码，请使用注册功能重新注册。");
        }
        if (!storedPassword.equals(password)) {
            throw new IllegalArgumentException("密码错误！");
        }
        return new PlayerLoginResult(existingPlayer, false);
    }

    private PlayerRecord mapPlayerRecord(ResultSet resultSet) throws SQLException
    {
        return new PlayerRecord(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDouble("max_weight"),
                resultSet.getString("password") != null ? resultSet.getString("password") : "",
                resultSet.getString("created_at"),
                resultSet.getString("updated_at")
        );
    }

    private String normalizeName(String name)
    {
        if (name == null) {
            return null;
        }
        String trimmedName = name.trim();
        return trimmedName.isEmpty() ? null : trimmedName;
    }

    private boolean isUniqueConstraintViolation(SQLException exception)
    {
        return exception.getMessage() != null
                && exception.getMessage().contains("UNIQUE constraint failed");
    }
}
