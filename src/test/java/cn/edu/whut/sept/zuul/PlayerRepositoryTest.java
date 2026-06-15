package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PlayerRepositoryTest {

    private PlayerRepository createRepository() throws Exception
    {
        File tempDb = File.createTempFile("zuul-player-test-", ".db");
        tempDb.deleteOnExit();

        DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
        databaseManager.initialize();
        return new PlayerRepository(databaseManager);
    }

    @Test
    public void testCreatePlayer() throws Exception
    {
        PlayerRepository repository = createRepository();

        PlayerRecord player = repository.createPlayer("Alice");

        assertNotNull(player);
        assertEquals("Alice", player.getName());
        assertTrue(player.getId() > 0);
        assertEquals(10.0, player.getMaxWeight(), 0.001);
    }

    @Test
    public void testFindByName() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("Bob");

        PlayerRecord found = repository.findByName("Bob");
        PlayerRecord missing = repository.findByName("Charlie");

        assertNotNull(found);
        assertEquals("Bob", found.getName());
        assertNull(missing);
    }

    @Test
    public void testLoginCreatesNewPlayer() throws Exception
    {
        PlayerRepository repository = createRepository();

        PlayerLoginResult result = repository.login("NewHero");

        assertTrue(result.isNewlyCreated());
        assertEquals("NewHero", result.getPlayer().getName());
    }

    @Test
    public void testLoginExistingPlayer() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("ExistingHero");

        PlayerLoginResult result = repository.login("ExistingHero");

        assertFalse(result.isNewlyCreated());
        assertEquals("ExistingHero", result.getPlayer().getName());
    }

    @Test
    public void testDuplicateUsernameDoesNotCrash() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("DuplicateUser");

        try {
            repository.createPlayer("DuplicateUser");
        } catch (DuplicatePlayerException e) {
            assertTrue(e.getMessage().contains("DuplicateUser"));
        }

        PlayerLoginResult firstLogin = repository.login("DuplicateUser");
        PlayerLoginResult secondLogin = repository.login("DuplicateUser");

        assertFalse(firstLogin.isNewlyCreated());
        assertFalse(secondLogin.isNewlyCreated());
        assertEquals(firstLogin.getPlayer().getId(), secondLogin.getPlayer().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyUsernameRejected() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.login("   ");
    }
}
