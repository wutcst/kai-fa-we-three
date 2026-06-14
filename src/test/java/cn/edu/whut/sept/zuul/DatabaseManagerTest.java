package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        assertTrue(tableExists(databaseManager, "game_save"));
        assertTrue(tableExists(databaseManager, "inventory_item"));
        assertTrue(tableExists(databaseManager, "room_item"));
    }

    @Test
    public void testInitializeIsIdempotent() throws Exception
    {
        File tempDb = File.createTempFile("zuul-test-idempotent-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());

        databaseManager.initialize();
        databaseManager.initialize();

        assertTrue(tableExists(databaseManager, "game_save"));
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
