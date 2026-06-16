package cn.edu.whut.sept.zuul;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责游戏世界配置（房间、出口、物品）的数据库读写。
 */
public class WorldDataRepository
{
    private final DatabaseManager databaseManager;

    public WorldDataRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public boolean isWorldEmpty() throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM world_room")) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    /**
     * 若世界配置为空，则插入默认房间、出口和物品数据。
     */
    public void seedDefaultWorldIfEmpty() throws SQLException
    {
        if (!isWorldEmpty()) {
            return;
        }

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                insertDefaultWorld(connection);
                connection.commit();
                System.out.println("已自动初始化默认游戏世界配置到数据库。");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 从数据库加载房间、出口和初始物品，构建游戏世界。
     */
    public WorldLoadResult loadWorld() throws SQLException
    {
        Map<Long, Room> roomsById = new HashMap<>();
        Map<String, Room> roomsByDescription = new HashMap<>();
        Map<String, Long> roomIdsByName = new HashMap<>();
        Room startRoom = null;

        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet roomResult = statement.executeQuery(
                     "SELECT id, name, description, is_start_room, room_type FROM world_room")) {
            List<RoomRecord> roomRecords = new ArrayList<>();
            while (roomResult.next()) {
                roomRecords.add(new RoomRecord(
                        roomResult.getLong("id"),
                        roomResult.getString("name"),
                        roomResult.getString("description"),
                        roomResult.getInt("is_start_room") == 1,
                        roomResult.getString("room_type")
                ));
            }

            List<Room> normalRooms = new ArrayList<>();
            for (RoomRecord record : roomRecords) {
                if (!"teleport".equals(record.roomType)) {
                    Room room = new Room(record.description);
                    roomsById.put(record.id, room);
                    roomsByDescription.put(record.description, room);
                    roomIdsByName.put(record.name, record.id);
                    normalRooms.add(room);
                    if (record.startRoom) {
                        startRoom = room;
                    }
                }
            }

            for (RoomRecord record : roomRecords) {
                if ("teleport".equals(record.roomType)) {
                    List<Room> teleportTargets = new ArrayList<>(normalRooms);
                    TeleportRoom teleportRoom = new TeleportRoom(record.description, teleportTargets);
                    roomsById.put(record.id, teleportRoom);
                    roomsByDescription.put(record.description, teleportRoom);
                    roomIdsByName.put(record.name, record.id);
                    teleportTargets.add(teleportRoom);
                }
            }
        }

        loadExits(roomsById);
        loadInitialRoomItems(roomsById);

        if (startRoom == null) {
            throw new SQLException("No start room found in world_room table.");
        }

        return new WorldLoadResult(roomsByDescription, startRoom);
    }

    private void loadExits(Map<Long, Room> roomsById) throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT from_room_id, direction, to_room_id FROM world_room_exit")) {
            while (resultSet.next()) {
                Room fromRoom = roomsById.get(resultSet.getLong("from_room_id"));
                Room toRoom = roomsById.get(resultSet.getLong("to_room_id"));
                if (fromRoom != null && toRoom != null) {
                    fromRoom.setExit(resultSet.getString("direction"), toRoom);
                }
            }
        }
    }

    private void loadInitialRoomItems(Map<Long, Room> roomsById) throws SQLException
    {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT wri.room_id, wi.name, wi.weight "
                             + "FROM world_room_item wri "
                             + "JOIN world_item wi ON wri.item_id = wi.id")) {
            while (resultSet.next()) {
                Room room = roomsById.get(resultSet.getLong("room_id"));
                if (room != null) {
                    room.addItem(new Item(
                            resultSet.getString("name"),
                            (int) Math.round(resultSet.getDouble("weight"))
                    ));
                }
            }
        }
    }

    private void insertDefaultWorld(Connection connection) throws SQLException
    {
        Map<String, Long> roomIds = insertDefaultRooms(connection);
        insertDefaultItems(connection, roomIds);
        insertDefaultExits(connection, roomIds);
    }

    private Map<String, Long> insertDefaultRooms(Connection connection) throws SQLException
    {
        String[][] rooms = {
                {"outside", "outside the main entrance of the university", "1", "normal"},
                {"theater", "in a lecture theater", "0", "normal"},
                {"pub", "in the campus pub", "0", "normal"},
                {"lab", "in a computing lab", "0", "normal"},
                {"office", "in the computing admin office", "0", "normal"},
                {"teleport", "in a mysterious teleport chamber", "0", "teleport"}
        };

        Map<String, Long> roomIds = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO world_room (name, description, is_start_room, room_type) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            for (String[] room : rooms) {
                statement.setString(1, room[0]);
                statement.setString(2, room[1]);
                statement.setInt(3, Integer.parseInt(room[2]));
                statement.setString(4, room[3]);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        roomIds.put(room[0], keys.getLong(1));
                    }
                }
            }
        }
        return roomIds;
    }

    private void insertDefaultItems(Connection connection, Map<String, Long> roomIds) throws SQLException
    {
        String[][] items = {
                {"campus_map", "Campus map of the university", "1"},
                {"welcome_brochure", "Welcome brochure for new students", "1"},
                {"notebook", "Lecture notebook", "2"},
                {"pen", "A blue pen", "1"},
                {"cookie", "Magic cookie", "1"},
                {"energy_drink", "Energy drink", "2"},
                {"task_item", "Important task item for the lab", "2"},
                {"programming_manual", "Java programming manual", "3"},
                {"usb_drive", "USB drive with course materials", "1"},
                {"keycard", "Office access keycard", "1"},
                {"report_template", "Project report template", "2"},
                {"teleport_shard", "A shard from the teleport chamber", "1"}
        };

        Map<String, Long> itemIds = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO world_item (name, description, weight) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            for (String[] item : items) {
                statement.setString(1, item[0]);
                statement.setString(2, item[1]);
                statement.setDouble(3, Double.parseDouble(item[2]));
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        itemIds.put(item[0], keys.getLong(1));
                    }
                }
            }
        }

        String[][] roomItems = {
                {"outside", "campus_map"},
                {"outside", "welcome_brochure"},
                {"theater", "notebook"},
                {"theater", "pen"},
                {"pub", "cookie"},
                {"pub", "energy_drink"},
                {"lab", "task_item"},
                {"lab", "programming_manual"},
                {"lab", "usb_drive"},
                {"office", "keycard"},
                {"office", "report_template"},
                {"teleport", "teleport_shard"}
        };

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO world_room_item (room_id, item_id) VALUES (?, ?)")) {
            for (String[] roomItem : roomItems) {
                statement.setLong(1, roomIds.get(roomItem[0]));
                statement.setLong(2, itemIds.get(roomItem[1]));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertDefaultExits(Connection connection, Map<String, Long> roomIds) throws SQLException
    {
        String[][] exits = {
                {"outside", "east", "theater"},
                {"outside", "south", "lab"},
                {"outside", "west", "pub"},
                {"theater", "west", "outside"},
                {"theater", "north", "teleport"},
                {"theater", "east", "office"},
                {"pub", "east", "outside"},
                {"lab", "north", "outside"},
                {"lab", "east", "office"},
                {"office", "west", "lab"},
                {"office", "south", "theater"}
        };

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO world_room_exit (from_room_id, direction, to_room_id) VALUES (?, ?, ?)")) {
            for (String[] exit : exits) {
                statement.setLong(1, roomIds.get(exit[0]));
                statement.setString(2, exit[1]);
                statement.setLong(3, roomIds.get(exit[2]));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static class RoomRecord
    {
        private final long id;
        private final String name;
        private final String description;
        private final boolean startRoom;
        private final String roomType;

        private RoomRecord(long id, String name, String description, boolean startRoom, String roomType)
        {
            this.id = id;
            this.name = name;
            this.description = description;
            this.startRoom = startRoom;
            this.roomType = roomType;
        }
    }
}
