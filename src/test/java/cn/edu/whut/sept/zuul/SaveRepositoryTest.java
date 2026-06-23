package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SaveRepositoryTest {

    private static class TestContext
    {
        private final DatabaseManager databaseManager;
        private final PlayerRepository playerRepository;
        private final SaveRepository saveRepository;
        private final PlayerRecord player;

        private TestContext() throws Exception
        {
            File tempDb = File.createTempFile("zuul-save-test-", ".db");
            tempDb.deleteOnExit();
            databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
            databaseManager.initialize();
            playerRepository = new PlayerRepository(databaseManager);
            saveRepository = new SaveRepository(databaseManager);
            player = playerRepository.createPlayer("SaveHero", "test-password");
        }
    }

    @Test
    public void testSaveAndLoadFullGameState() throws Exception
    {
        TestContext context = new TestContext();
        GameSnapshot snapshot = buildFullSnapshot();

        context.saveRepository.saveGame(context.player.getId(), "slot1", snapshot);
        GameSaveRecord loaded = context.saveRepository.loadGame(context.player.getId(), "slot1");

        assertNotNull(loaded);
        assertEquals("slot1", loaded.getSaveName());
        assertEquals("in a computing lab", loaded.getCurrentRoomName());
        assertEquals(25, loaded.getScore());
        assertEquals(88, loaded.getHealth());
        assertEquals(15, loaded.getMaxWeight());
        assertEquals(7, loaded.getCurrentWeight());
        assertEquals(1, loaded.getInventoryItems().size());
        assertEquals("sword", loaded.getInventoryItems().get(0).getDescription());
        assertEquals(1, loaded.getRoomItems().size());
        assertEquals("cookie", loaded.getRoomItems().get(0).getItemName());
        assertEquals("collected_task_item", loaded.getQuestProgress().get("main_quest"));
    }

    @Test
    public void testUpdateExistingSave() throws Exception
    {
        TestContext context = new TestContext();
        context.saveRepository.saveGame(context.player.getId(), "slot1", buildBasicSnapshot());

        GameSnapshot updatedSnapshot = buildFullSnapshot();
        context.saveRepository.saveGame(context.player.getId(), "slot1", updatedSnapshot);

        GameSaveRecord loaded = context.saveRepository.loadGame(context.player.getId(), "slot1");
        assertEquals(25, loaded.getScore());
        assertEquals(88, loaded.getHealth());
        assertEquals("collected_task_item", loaded.getQuestProgress().get("main_quest"));
    }

    @Test
    public void testListAndDeleteSaves() throws Exception
    {
        TestContext context = new TestContext();
        context.saveRepository.saveGame(context.player.getId(), "slot1", buildBasicSnapshot());
        context.saveRepository.saveGame(context.player.getId(), "slot2", buildBasicSnapshot());

        List<GameSaveRecord> saves = context.saveRepository.listSaves(context.player.getId());
        assertEquals(2, saves.size());

        assertTrue(context.saveRepository.deleteSave(context.player.getId(), "slot1"));
        assertFalse(context.saveRepository.deleteSave(context.player.getId(), "missing"));
        assertNull(context.saveRepository.loadGame(context.player.getId(), "slot1"));
        assertNotNull(context.saveRepository.loadGame(context.player.getId(), "slot2"));
    }

    private GameSnapshot buildBasicSnapshot()
    {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.setCurrentRoomName("outside the main entrance of the university");
        snapshot.setScore(0);
        snapshot.setHealth(100);
        snapshot.setMaxWeight(10);
        snapshot.setCurrentWeight(0);
        snapshot.getQuestProgress().put("main_quest", "started");
        return snapshot;
    }

    private GameSnapshot buildFullSnapshot()
    {
        GameSnapshot snapshot = buildBasicSnapshot();
        snapshot.setCurrentRoomName("in a computing lab");
        snapshot.setScore(25);
        snapshot.setHealth(88);
        snapshot.setMaxWeight(15);
        snapshot.setCurrentWeight(7);
        snapshot.getInventoryItems().add(new Item("sword", 7));
        snapshot.getRoomItems().add(new RoomItemSnapshot("in the campus pub", "cookie", 1));
        Map<String, String> questProgress = new HashMap<>();
        questProgress.put("main_quest", "collected_task_item");
        snapshot.setQuestProgress(questProgress);
        return snapshot;
    }
}
