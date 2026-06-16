package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WorldDataRepositoryTest {

    @Test
    public void testSeedAndLoadWorldFromDatabase() throws Exception
    {
        File tempDb = File.createTempFile("zuul-world-test-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();

        WorldDataRepository repository = new WorldDataRepository(databaseManager);
        assertTrue(repository.isWorldEmpty());
        repository.seedDefaultWorldIfEmpty();
        assertTrue(!repository.isWorldEmpty());

        WorldLoadResult world = repository.loadWorld();
        Room startRoom = world.getStartRoom();

        assertNotNull(startRoom);
        assertEquals("outside the main entrance of the university", startRoom.getShortDescription());
        assertTrue(startRoom.getLongDescription().contains("campus_map"));
        assertNotNull(startRoom.getExit("east"));
        assertEquals("in a lecture theater", startRoom.getExit("east").getShortDescription());

        Room lab = world.getRoomsByDescription().get("in a computing lab");
        assertNotNull(lab);
        assertNotNull(lab.getExit("east"));
    }

    @Test
    public void testSeedIsIdempotent() throws Exception
    {
        File tempDb = File.createTempFile("zuul-world-idempotent-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();

        WorldDataRepository repository = new WorldDataRepository(databaseManager);
        repository.seedDefaultWorldIfEmpty();
        repository.seedDefaultWorldIfEmpty();

        WorldLoadResult world = repository.loadWorld();
        assertEquals(6, world.getRoomsByDescription().size());
    }

    @Test
    public void testGameLoadsWorldFromDatabase() throws Exception
    {
        File tempDb = File.createTempFile("zuul-game-world-test-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();

        Game game = new Game(databaseManager);

        assertNotNull(game.getCurrentRoom());
        assertTrue(game.getCurrentRoom().getLongDescription().contains("outside"));
        assertNotNull(game.findRoomByDescription("in a computing lab"));
    }
}
