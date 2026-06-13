package cn.edu.whut.sept.zuul;

/**
 * CommandResult represents the execution result of a game command.
 * It is used by the GUI layer and test layer to obtain command feedback.
 */
public class CommandResult {
    private final boolean success;
    private final String message;
    private final GameState gameState;

    public CommandResult(boolean success, String message, GameState gameState) {
        this.success = success;
        this.message = message;
        this.gameState = gameState;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public GameState getGameState() {
        return gameState;
    }
}