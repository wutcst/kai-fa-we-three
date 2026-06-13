package cn.edu.whut.sept.zuul;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameServiceTest {

    @Test
    public void testExecuteValidCommand() {
        GameService gameService = new GameService();

        CommandResult result = gameService.executeCommand("look");

        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        assertNotNull(result.getGameState());
    }

    @Test
    public void testExecuteEmptyCommand() {
        GameService gameService = new GameService();

        CommandResult result = gameService.executeCommand("");

        assertFalse(result.isSuccess());
        assertEquals("命令不能为空。", result.getMessage());
    }

    @Test
    public void testSetAndGetCurrentState() {
        GameService gameService = new GameService();
        GameState state = new GameState();
        state.setPlayerName("testPlayer");
        state.setCurrentRoomName("testRoom");

        gameService.setCurrentState(state);

        assertEquals("testPlayer", gameService.getCurrentState().getPlayerName());
        assertEquals("testRoom", gameService.getCurrentState().getCurrentRoomName());
    }
}