package cn.edu.whut.sept.zuul;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SaveServiceTest {

    private static class TestContext
    {
        private final Game game;
        private final SaveService saveService;
        private final PlayerRepository playerRepository;

        private TestContext() throws Exception
        {
            File tempDb = File.createTempFile("zuul-save-service-test-", ".db");
            tempDb.deleteOnExit();
            DatabaseManager databaseManager = new DatabaseManager(tempDb.getAbsolutePath());
            databaseManager.initialize();
            playerRepository = new PlayerRepository(databaseManager);
            game = new Game(databaseManager);
            saveService = game.getSaveService();
        }

        private void login(String username) throws Exception
        {
            game.applyPlayerLogin(playerRepository.login(username));
        }
    }

    @Test(expected = SaveException.class)
    public void testSaveRequiresLogin() throws Exception
    {
        TestContext context = new TestContext();
        context.saveService.save(context.game, "slot1");
    }

    @Test
    public void testSaveLoadRestoreState() throws Exception
    {
        TestContext context = new TestContext();
        context.login("Hero");

        Room lab = context.game.findRoomByDescription("in a computing lab");
        context.game.setCurrentRoom(lab);
        context.game.setHp(75);
        context.game.setScore(30);
        context.game.getPlayer().setMaxWeight(20);
        context.game.getPlayer().takeItem(new Item("sword", 5));
        context.game.updateQuestProgress("main_quest", "collected_task_item");
        lab.removeItem("task_item");

        context.saveService.save(context.game, "slot1");

        context.game.setCurrentRoom(context.game.findRoomByDescription("in the campus pub"));
        context.game.setHp(20);
        context.game.setScore(0);
        context.game.getPlayer().clearInventory();
        context.game.getPlayer().setMaxWeight(10);
        context.game.updateQuestProgress("main_quest", "started");
        lab.addItem(new Item("task_item", 2));

        context.saveService.load(context.game, "slot1");

        assertEquals("in a computing lab",
                context.game.getCurrentRoom().getShortDescription());
        assertEquals(75, context.game.getHp());
        assertEquals(30, context.game.getScore());
        assertEquals(20, context.game.getPlayer().getMaxWeight());
        assertEquals(5, context.game.getPlayer().getCurrentWeight());
        assertEquals(1, context.game.getPlayer().getInventoryItems().size());
        assertEquals("sword", context.game.getPlayer().getInventoryItems().get(0).getDescription());
        assertEquals("collected_task_item", context.game.getQuestProgressValue("main_quest"));
        assertNull(lab.removeItem("task_item"));
    }

    @Test
    public void testListAndDeleteSaveWithMessages() throws Exception
    {
        TestContext context = new TestContext();
        context.login("Hero");

        context.saveService.save(context.game, "slot1");
        String listMessage = context.saveService.listSaves(context.game);
        assertTrue(listMessage.contains("slot1"));

        String deleteMessage = context.saveService.deleteSave(context.game, "slot1");
        assertTrue(deleteMessage.contains("已删除"));
        assertTrue(context.saveService.listSaves(context.game).contains("没有可用存档"));
    }

    @Test(expected = SaveException.class)
    public void testLoadMissingSaveShowsError() throws Exception
    {
        TestContext context = new TestContext();
        context.login("Hero");
        context.saveService.load(context.game, "missing");
    }

    @Test(expected = SaveException.class)
    public void testDeleteMissingSaveShowsError() throws Exception
    {
        TestContext context = new TestContext();
        context.login("Hero");
        context.saveService.deleteSave(context.game, "missing");
    }
}
