package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;
import java.util.List;

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
            player = playerRepository.createPlayer("SaveHero");
        }
    }

    @Test
    public void testSaveAndLoadGame() throws Exception
    {
        TestContext context = new TestContext();
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.setCurrentRoomName("outside the main entrance of the university");
        snapshot.setScore(25);
        snapshot.setHealth(88);
        snapshot.setMaxWeight(15);
        snapshot.setCurrentWeight(5);
        snapshot.setVictory(false);
        snapshot.getInventoryItems().add(new Item("sword", 5));

        context.saveRepository.saveGame(context.player.getId(), "slot1", snapshot);
        GameSaveRecord loaded = context.saveRepository.loadGame(context.player.getId(), "slot1");

        assertNotNull(loaded);
        assertEquals("slot1", loaded.getSaveName());
        assertEquals("outside the main entrance of the university", loaded.getCurrentRoomName());
        assertEquals(25, loaded.getScore());
        assertEquals(88, loaded.getHealth());
        assertEquals(15, loaded.getMaxWeight());
        assertEquals(5, loaded.getCurrentWeight());
        assertEquals(1, loaded.getInventoryItems().size());
        assertEquals("sword", loaded.getInventoryItems().get(0).getDescription());
    }

    @Test
    public void testUpdateExistingSave() throws Exception
    {
        TestContext context = new TestContext();

        GameSnapshot firstSnapshot = new GameSnapshot();
        firstSnapshot.setCurrentRoomName("outside the main entrance of the university");
        firstSnapshot.setScore(10);
        firstSnapshot.setHealth(100);
        firstSnapshot.setMaxWeight(10);
        firstSnapshot.setCurrentWeight(0);
        context.saveRepository.saveGame(context.player.getId(), "slot1", firstSnapshot);

        GameSnapshot secondSnapshot = new GameSnapshot();
        secondSnapshot.setCurrentRoomName("in the campus pub");
        secondSnapshot.setScore(40);
        secondSnapshot.setHealth(60);
        secondSnapshot.setMaxWeight(20);
        secondSnapshot.setCurrentWeight(1);
        secondSnapshot.getInventoryItems().add(new Item("cookie", 1));
        context.saveRepository.saveGame(context.player.getId(), "slot1", secondSnapshot);

        GameSaveRecord loaded = context.saveRepository.loadGame(context.player.getId(), "slot1");
        assertEquals("in the campus pub", loaded.getCurrentRoomName());
        assertEquals(40, loaded.getScore());
        assertEquals(60, loaded.getHealth());
        assertEquals(1, loaded.getInventoryItems().size());
    }

    @Test
    public void testListAndDeleteSaves() throws Exception
    {
        TestContext context = new TestContext();
        GameSnapshot snapshot = buildBasicSnapshot();
        context.saveRepository.saveGame(context.player.getId(), "slot1", snapshot);
        context.saveRepository.saveGame(context.player.getId(), "slot2", snapshot);

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
        return snapshot;
    }
}
