package cn.edu.whut.sept.zuul;

/**
 * GameService is the unified service layer of the game.
 * GUI, database and tests should interact with the game through this class.
 */
public class GameService {
    private GameState currentState;

    public GameService() {
        this.currentState = new GameState();
    }

    /**
     * Execute a command entered by the player.
     *
     * @param input command text
     * @return command execution result
     */
    public CommandResult executeCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new CommandResult(false, "命令不能为空。", currentState);
        }

        String command = input.trim();

        // Temporary implementation for UI and tests.
        // Later this method can be connected with the original game command system.
        return new CommandResult(true, "已执行命令：" + command, currentState);
    }

    /**
     * Get current game state for GUI display and database saving.
     *
     * @return current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Set current game state. This method is mainly used after loading a saved game.
     *
     * @param gameState loaded game state
     */
    public void setCurrentState(GameState gameState) {
        if (gameState != null) {
            this.currentState = gameState;
        }
    }
}