package cn.edu.whut.sept.zuul;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 管理 SQLite 数据库连接与初始化建表。
 */
public class DatabaseManager
{
    private static final String DEFAULT_DB_PATH = "data/zuul.db";
    private final String jdbcUrl;

    public DatabaseManager()
    {
        this(DEFAULT_DB_PATH);
    }

    public DatabaseManager(String dbPath)
    {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
    }

    /**
     * 获取 SQLite 数据库连接。
     */
    public Connection getConnection() throws SQLException
    {
        ensureDatabaseDirectory();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    /**
     * 初始化数据库表结构。重复调用是安全的。
     */
    public void initialize() throws SQLException
    {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS game_save ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "player_name TEXT NOT NULL, "
                            + "current_room_name TEXT NOT NULL, "
                            + "score INTEGER NOT NULL DEFAULT 0, "
                            + "health INTEGER NOT NULL DEFAULT 100, "
                            + "current_weight REAL NOT NULL DEFAULT 0, "
                            + "max_weight REAL NOT NULL DEFAULT 10, "
                            + "updated_at TEXT NOT NULL"
                            + ")"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS inventory_item ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "save_id INTEGER NOT NULL, "
                            + "item_name TEXT NOT NULL, "
                            + "weight REAL NOT NULL, "
                            + "FOREIGN KEY (save_id) REFERENCES game_save(id)"
                            + ")"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS room_item ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "save_id INTEGER NOT NULL, "
                            + "room_name TEXT NOT NULL, "
                            + "item_name TEXT NOT NULL, "
                            + "weight REAL NOT NULL, "
                            + "FOREIGN KEY (save_id) REFERENCES game_save(id)"
                            + ")"
            );
        }
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    private void ensureDatabaseDirectory()
    {
        if (":memory:".equals(jdbcUrl.substring("jdbc:sqlite:".length()))) {
            return;
        }

        File dbFile = new File(jdbcUrl.substring("jdbc:sqlite:".length()));
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
