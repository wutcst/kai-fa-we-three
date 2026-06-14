package cn.edu.whut.sept.zuul;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
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
            for (String sql : loadSchemaStatements()) {
                statement.execute(sql);
            }
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
