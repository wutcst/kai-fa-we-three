package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {

    @Test
    public void testConnectAndInitializeDatabase() throws Exception
    {
        File tempDb = File.createTempFile("zuul-test-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());

        databaseManager.initialize();

        try (Connection connection = databaseManager.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(1));
        }

        assertTrue(tableExists(databaseManager, "player"));
        assertTrue(tableExists(databaseManager, "game_save"));
        assertTrue(tableExists(databaseManager, "inventory_item"));
        assertTrue(tableExists(databaseManager, "room_item"));
        assertTrue(tableExists(databaseManager, "quest_progress"));
    }

    @Test
    public void testMigrateLegacyGameSaveSchema() throws Exception
    {
        File tempDb = File.createTempFile("zuul-legacy-migrate-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE game_save ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "player_name TEXT NOT NULL, "
                            + "current_room_name TEXT NOT NULL, "
                            + "score INTEGER NOT NULL DEFAULT 0, "
                            + "health INTEGER NOT NULL DEFAULT 100, "
                            + "current_weight REAL NOT NULL DEFAULT 0, "
                            + "saved_at TEXT NOT NULL)"
            );
        }

        databaseManager.initialize();

        assertTrue(tableExists(databaseManager, "game_save"));
        assertColumnsExist(databaseManager, "game_save",
                "id", "player_id", "save_name", "current_room_name", "score", "health",
                "current_weight", "max_weight", "is_victory", "saved_at");
    }

    @Test
    public void testInitializeIsIdempotent() throws Exception
    {
        File tempDb = File.createTempFile("zuul-test-idempotent-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());

        databaseManager.initialize();
        databaseManager.initialize();

        assertTrue(tableExists(databaseManager, "player"));
        assertTrue(tableExists(databaseManager, "game_save"));
        assertTrue(tableExists(databaseManager, "inventory_item"));
        assertTrue(tableExists(databaseManager, "room_item"));
        assertTrue(tableExists(databaseManager, "quest_progress"));
    }

    @Test
    public void testSchemaSupportsPlayerSaveAndInventoryData() throws Exception
    {
        File tempDb = File.createTempFile("zuul-test-data-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();

        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "INSERT INTO player (name, max_weight, created_at, updated_at) "
                            + "VALUES ('Adventurer', 10, '2026-06-14T12:00:00', '2026-06-14T12:00:00')"
            );
            statement.execute(
                    "INSERT INTO game_save (player_id, save_name, current_room_name, score, health, "
                            + "current_weight, max_weight, is_victory, saved_at) "
                            + "VALUES (1, 'default', 'outside', 20, 80, 5, 10, 0, '2026-06-14T12:05:00')"
            );
            statement.execute(
                    "INSERT INTO inventory_item (save_id, item_name, weight) VALUES (1, 'sword', 5)"
            );

            ResultSet playerResult = statement.executeQuery("SELECT name, max_weight FROM player WHERE id = 1");
            assertTrue(playerResult.next());
            assertTrue("Adventurer".equals(playerResult.getString("name")));
            assertTrue(Math.abs(playerResult.getDouble("max_weight") - 10) < 0.001);

            ResultSet saveResult = statement.executeQuery(
                    "SELECT current_room_name, health FROM game_save WHERE id = 1");
            assertTrue(saveResult.next());
            assertTrue("outside".equals(saveResult.getString("current_room_name")));
            assertTrue(saveResult.getInt("health") == 80);

            ResultSet itemResult = statement.executeQuery(
                    "SELECT item_name, weight FROM inventory_item WHERE save_id = 1");
            assertTrue(itemResult.next());
            assertTrue("sword".equals(itemResult.getString("item_name")));
            assertTrue(Math.abs(itemResult.getDouble("weight") - 5) < 0.001);
        }
    }

    @Test
    public void testSchemaContainsRequiredColumns() throws Exception
    {
        File tempDb = File.createTempFile("zuul-test-columns-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();

        assertColumnsExist(databaseManager, "player",
                "id", "name", "max_weight", "created_at", "updated_at");
        assertColumnsExist(databaseManager, "game_save",
                "id", "player_id", "save_name", "current_room_name", "score", "health",
                "current_weight", "max_weight", "is_victory", "saved_at");
        assertColumnsExist(databaseManager, "inventory_item",
                "id", "save_id", "item_name", "weight");
    }

    private void assertColumnsExist(DatabaseManager databaseManager, String tableName,
                                    String... expectedColumns) throws SQLException
    {
        Set<String> actualColumns = new HashSet<>();
        try (Connection connection = databaseManager.getConnection();
             ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, null)) {
            while (resultSet.next()) {
                actualColumns.add(resultSet.getString("COLUMN_NAME"));
            }
        }

        List<String> expected = Arrays.asList(expectedColumns);
        for (String column : expected) {
            assertTrue("Missing column " + column + " in table " + tableName,
                    actualColumns.contains(column));
        }
    }

    private boolean tableExists(DatabaseManager databaseManager, String tableName) throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'")) {
            return resultSet.next();
        }
    }
}
