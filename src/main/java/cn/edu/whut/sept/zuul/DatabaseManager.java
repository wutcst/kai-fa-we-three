package cn.edu.whut.sept.zuul;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理 SQLite 数据库连接与初始化建表。
 */
public class DatabaseManager
{
    private static final String DEFAULT_DB_PATH = "data/zuul.db";
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";
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
            migrateIfNeeded(connection);
            for (String sql : loadSchemaStatements()) {
                statement.execute(sql);
            }
        }
    }

    /**
     * 检测并迁移旧版数据库结构，避免旧表缺少新字段导致初始化失败。
     */
    private void migrateIfNeeded(Connection connection) throws SQLException
    {
        if (!tableExists(connection, "game_save")) {
            return;
        }

        boolean needsMigration = !columnExists(connection, "game_save", "player_id")
                || !columnExists(connection, "game_save", "max_weight")
                || !columnExists(connection, "game_save", "save_name");

        if (needsMigration) {
            dropSaveRelatedTables(connection);
            System.out.println("检测到旧版存档结构，已自动重建存档相关数据表（旧存档数据已清除）。");
        }
    }

    private void dropSaveRelatedTables(Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS quest_progress");
            statement.execute("DROP TABLE IF EXISTS room_item");
            statement.execute("DROP TABLE IF EXISTS inventory_item");
            statement.execute("DROP TABLE IF EXISTS game_save");
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException
    {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName)
            throws SQLException
    {
        try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    static List<String> loadSchemaStatements() throws SQLException
    {
        InputStream inputStream = DatabaseManager.class.getResourceAsStream(SCHEMA_RESOURCE);
        if (inputStream == null) {
            throw new SQLException("Schema file not found: " + SCHEMA_RESOURCE);
        }

        StringBuilder sqlBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read schema file.", e);
        }

        List<String> statements = new ArrayList<>();
        for (String statement : sqlBuilder.toString().split(";")) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                statements.add(trimmedStatement);
            }
        }
        return statements;
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
