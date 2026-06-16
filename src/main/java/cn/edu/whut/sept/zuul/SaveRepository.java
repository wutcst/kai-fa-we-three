package cn.edu.whut.sept.zuul;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责 game_save 与 inventory_item 表的数据访问。
 */
public class SaveRepository
{
    private final DatabaseManager databaseManager;

    public SaveRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public long saveGame(long playerId, String saveName, GameSnapshot snapshot) throws SQLException
    {
        String savedAt = Instant.now().toString();
        GameSaveRecord existingSave = findSaveByName(playerId, saveName);

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long saveId;
                if (existingSave != null) {
                    saveId = existingSave.getId();
                    updateSave(connection, saveId, snapshot, savedAt);
                    deleteInventoryItems(connection, saveId);
                    deleteRoomItems(connection, saveId);
                    deleteQuestProgress(connection, saveId);
                } else {
                    saveId = insertSave(connection, playerId, saveName, snapshot, savedAt);
                }
                insertInventoryItems(connection, saveId, snapshot.getInventoryItems());
                insertRoomItems(connection, saveId, snapshot.getRoomItems());
                insertQuestProgress(connection, saveId, snapshot.getQuestProgress());
                connection.commit();
                return saveId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public GameSaveRecord loadGame(long playerId, String saveName) throws SQLException
    {
        GameSaveRecord saveRecord = findSaveByName(playerId, saveName);
        if (saveRecord == null) {
            return null;
        }

        List<Item> inventoryItems = findInventoryItems(saveRecord.getId());
        List<RoomItemSnapshot> roomItems = findRoomItems(saveRecord.getId());
        Map<String, String> questProgress = findQuestProgress(saveRecord.getId());
        return new GameSaveRecord(
                saveRecord.getId(),
                saveRecord.getPlayerId(),
                saveRecord.getSaveName(),
                saveRecord.getCurrentRoomName(),
                saveRecord.getScore(),
                saveRecord.getHealth(),
                saveRecord.getMaxWeight(),
                saveRecord.getCurrentWeight(),
                saveRecord.isVictory(),
                saveRecord.getSavedAt(),
                inventoryItems,
                roomItems,
                questProgress
        );
    }

    public List<GameSaveRecord> listSaves(long playerId) throws SQLException
    {
        List<GameSaveRecord> saves = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, player_id, save_name, current_room_name, score, health, "
                             + "max_weight, current_weight, is_victory, saved_at "
                             + "FROM game_save WHERE player_id = ? ORDER BY saved_at DESC")) {
            statement.setLong(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    saves.add(mapSaveRecord(resultSet, new ArrayList<Item>()));
                }
            }
        }
        return saves;
    }

    public boolean deleteSave(long playerId, String saveName) throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM game_save WHERE player_id = ? AND save_name = ?")) {
            statement.setLong(1, playerId);
            statement.setString(2, saveName);
            return statement.executeUpdate() > 0;
        }
    }

    private GameSaveRecord findSaveByName(long playerId, String saveName) throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, player_id, save_name, current_room_name, score, health, "
                             + "max_weight, current_weight, is_victory, saved_at "
                             + "FROM game_save WHERE player_id = ? AND save_name = ?")) {
            statement.setLong(1, playerId);
            statement.setString(2, saveName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSaveRecord(resultSet, null);
                }
            }
        }
        return null;
    }

    private List<Item> findInventoryItems(long saveId) throws SQLException
    {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT item_name, weight FROM inventory_item WHERE save_id = ? ORDER BY id")) {
            statement.setLong(1, saveId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new Item(
                            resultSet.getString("item_name"),
                            (int) Math.round(resultSet.getDouble("weight"))
                    ));
                }
            }
        }
        return items;
    }

    private List<RoomItemSnapshot> findRoomItems(long saveId) throws SQLException
    {
        List<RoomItemSnapshot> roomItems = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT room_name, item_name, weight FROM room_item "
                             + "WHERE save_id = ? ORDER BY id")) {
            statement.setLong(1, saveId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    roomItems.add(new RoomItemSnapshot(
                            resultSet.getString("room_name"),
                            resultSet.getString("item_name"),
                            (int) Math.round(resultSet.getDouble("weight"))
                    ));
                }
            }
        }
        return roomItems;
    }

    private Map<String, String> findQuestProgress(long saveId) throws SQLException
    {
        Map<String, String> questProgress = new HashMap<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT quest_key, progress_value FROM quest_progress WHERE save_id = ?")) {
            statement.setLong(1, saveId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questProgress.put(
                            resultSet.getString("quest_key"),
                            resultSet.getString("progress_value")
                    );
                }
            }
        }
        return questProgress;
    }

    private long insertSave(Connection connection, long playerId, String saveName,
                            GameSnapshot snapshot, String savedAt) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO game_save (player_id, save_name, current_room_name, score, health, "
                        + "current_weight, max_weight, is_victory, saved_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            bindSaveStatement(statement, playerId, saveName, snapshot, savedAt);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert save: " + saveName);
    }

    private void updateSave(Connection connection, long saveId, GameSnapshot snapshot,
                            String savedAt) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE game_save SET current_room_name = ?, score = ?, health = ?, "
                        + "current_weight = ?, max_weight = ?, is_victory = ?, saved_at = ? "
                        + "WHERE id = ?")) {
            statement.setString(1, snapshot.getCurrentRoomName());
            statement.setInt(2, snapshot.getScore());
            statement.setInt(3, snapshot.getHealth());
            statement.setDouble(4, snapshot.getCurrentWeight());
            statement.setDouble(5, snapshot.getMaxWeight());
            statement.setInt(6, snapshot.isVictory() ? 1 : 0);
            statement.setString(7, savedAt);
            statement.setLong(8, saveId);
            statement.executeUpdate();
        }
    }

    private void bindSaveStatement(PreparedStatement statement, long playerId, String saveName,
                                   GameSnapshot snapshot, String savedAt) throws SQLException
    {
        statement.setLong(1, playerId);
        statement.setString(2, saveName);
        statement.setString(3, snapshot.getCurrentRoomName());
        statement.setInt(4, snapshot.getScore());
        statement.setInt(5, snapshot.getHealth());
        statement.setDouble(6, snapshot.getCurrentWeight());
        statement.setDouble(7, snapshot.getMaxWeight());
        statement.setInt(8, snapshot.isVictory() ? 1 : 0);
        statement.setString(9, savedAt);
    }

    private void deleteInventoryItems(Connection connection, long saveId) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM inventory_item WHERE save_id = ?")) {
            statement.setLong(1, saveId);
            statement.executeUpdate();
        }
    }

    private void insertInventoryItems(Connection connection, long saveId, List<Item> items)
            throws SQLException
    {
        if (items == null || items.isEmpty()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO inventory_item (save_id, item_name, weight) VALUES (?, ?, ?)")) {
            for (Item item : items) {
                statement.setLong(1, saveId);
                statement.setString(2, item.getDescription());
                statement.setDouble(3, item.getWeight());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void deleteRoomItems(Connection connection, long saveId) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM room_item WHERE save_id = ?")) {
            statement.setLong(1, saveId);
            statement.executeUpdate();
        }
    }

    private void deleteQuestProgress(Connection connection, long saveId) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM quest_progress WHERE save_id = ?")) {
            statement.setLong(1, saveId);
            statement.executeUpdate();
        }
    }

    private void insertRoomItems(Connection connection, long saveId, List<RoomItemSnapshot> roomItems)
            throws SQLException
    {
        if (roomItems == null || roomItems.isEmpty()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO room_item (save_id, room_name, item_name, weight) VALUES (?, ?, ?, ?)")) {
            for (RoomItemSnapshot roomItem : roomItems) {
                statement.setLong(1, saveId);
                statement.setString(2, roomItem.getRoomName());
                statement.setString(3, roomItem.getItemName());
                statement.setDouble(4, roomItem.getWeight());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertQuestProgress(Connection connection, long saveId, Map<String, String> questProgress)
            throws SQLException
    {
        if (questProgress == null || questProgress.isEmpty()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO quest_progress (save_id, quest_key, progress_value) VALUES (?, ?, ?)")) {
            for (Map.Entry<String, String> entry : questProgress.entrySet()) {
                statement.setLong(1, saveId);
                statement.setString(2, entry.getKey());
                statement.setString(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private GameSaveRecord mapSaveRecord(ResultSet resultSet, List<Item> inventoryItems)
            throws SQLException
    {
        return new GameSaveRecord(
                resultSet.getLong("id"),
                resultSet.getLong("player_id"),
                resultSet.getString("save_name"),
                resultSet.getString("current_room_name"),
                resultSet.getInt("score"),
                resultSet.getInt("health"),
                (int) Math.round(resultSet.getDouble("max_weight")),
                (int) Math.round(resultSet.getDouble("current_weight")),
                resultSet.getInt("is_victory") == 1,
                resultSet.getString("saved_at"),
                inventoryItems,
                null,
                null
        );
    }
}
