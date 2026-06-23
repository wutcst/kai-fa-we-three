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

    private static final String TEST_PASSWORD = "test-password";

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

        PlayerRecord player = repository.createPlayer("Alice", TEST_PASSWORD);

        assertNotNull(player);
        assertEquals("Alice", player.getName());
        assertTrue(player.getId() > 0);
        assertEquals(10.0, player.getMaxWeight(), 0.001);
    }

    @Test
    public void testFindByName() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("Bob", TEST_PASSWORD);

        PlayerRecord found = repository.findByName("Bob");
        PlayerRecord missing = repository.findByName("Charlie");

        assertNotNull(found);
        assertEquals("Bob", found.getName());
        assertNull(missing);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginNonExistentUserFails() throws Exception
    {
        PlayerRepository repository = createRepository();
        // 不存在的用户登录应抛出异常
        repository.login("NewHero", TEST_PASSWORD);
    }

    @Test
    public void testLoginWrongPasswordFails() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("PasswordUser", "correct-password");

        try {
            repository.login("PasswordUser", "wrong-password");
            assertFalse("应抛出异常", true);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("密码错误"));
        }
    }

    @Test
    public void testRegisterNewPlayer() throws Exception
    {
        PlayerRepository repository = createRepository();
        PlayerRecord created = repository.createPlayer("RegisterHero", TEST_PASSWORD);
        assertNotNull(created);
        assertEquals("RegisterHero", created.getName());
        assertTrue(created.getId() > 0);
    }

    @Test(expected = DuplicatePlayerException.class)
    public void testRegisterDuplicateFails() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("DupRegister", TEST_PASSWORD);
        repository.createPlayer("DupRegister", TEST_PASSWORD);
    }

    @Test
    public void testLoginExistingPlayer() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("ExistingHero", TEST_PASSWORD);

        PlayerLoginResult result = repository.login("ExistingHero", TEST_PASSWORD);

        assertFalse(result.isNewlyCreated());
        assertEquals("ExistingHero", result.getPlayer().getName());
    }

    @Test
    public void testDuplicateUsernameDoesNotCrash() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.createPlayer("DuplicateUser", TEST_PASSWORD);

        try {
            repository.createPlayer("DuplicateUser", TEST_PASSWORD);
        } catch (DuplicatePlayerException e) {
            assertTrue(e.getMessage().contains("DuplicateUser"));
        }

        PlayerLoginResult firstLogin = repository.login("DuplicateUser", TEST_PASSWORD);
        PlayerLoginResult secondLogin = repository.login("DuplicateUser", TEST_PASSWORD);

        assertFalse(firstLogin.isNewlyCreated());
        assertFalse(secondLogin.isNewlyCreated());
        assertEquals(firstLogin.getPlayer().getId(), secondLogin.getPlayer().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyUsernameRejected() throws Exception
    {
        PlayerRepository repository = createRepository();
        repository.login("   ", TEST_PASSWORD);
    }
}
