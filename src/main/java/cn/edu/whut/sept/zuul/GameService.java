package cn.edu.whut.sept.zuul;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * GameService is the unified service layer of the game.
 * GUI, database and tests should interact with the game through this class.
 */
@Service
public class GameService {
    private GameState currentState;
    private Game game;
    private CommandWords commandWords;

    public GameService() {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.initialize();
            this.game = new Game(databaseManager);
            this.commandWords = new CommandWords(game.getPlayerRepository(), game.getSaveService());
            syncStateFromGame();
        } catch (Exception e) {
            this.currentState = new GameState();
            throw new IllegalStateException("Failed to initialize game service.", e);
        }
    }

    /**
     * Execute a command entered by the player.
     *
     * @param input command text
     * @return command execution result
     */
    public synchronized CommandResult executeCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new CommandResult(false, "\u547d\u4ee4\u4e0d\u80fd\u4e3a\u7a7a\u3002", currentState);
        }

        String commandText = input.trim();
        ParsedCommand parsedCommand = parse(commandText);
        Command command = commandWords.get(parsedCommand.word);
        if (command == null) {
            return new CommandResult(false, "I don't understand: " + parsedCommand.word, currentState);
        }

        command.setSecondWord(parsedCommand.secondWord);
        String message = captureCommandOutput(command);
        game.checkVictory();
        syncStateFromGame();
        return new CommandResult(true, message, currentState);
    }

    /**
     * Get current game state for GUI display and database saving.
     *
     * @return current game state
     */
    public synchronized GameState getCurrentState() {
        return currentState;
    }

    /**
     * Reset the game world to its initial state.
     * All items return to their original rooms, HP/score reset, etc.
     */
    public synchronized void resetGame() {
        game.resetWorld();
        syncStateFromGame();
    }

    /**
     * Set current game state. This method is mainly used after loading a saved game.
     *
     * @param gameState loaded game state
     */
    public synchronized void setCurrentState(GameState gameState) {
        if (gameState != null) {
            this.currentState = gameState;
        }
    }

    private ParsedCommand parse(String input) {
        Scanner scanner = new Scanner(input);
        String word = scanner.hasNext() ? scanner.next() : "";
        String secondWord = scanner.hasNext() ? scanner.next() : null;
        scanner.close();
        return new ParsedCommand(word, secondWord);
    }

    private String captureCommandOutput(Command command) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer, true);
        try {
            System.setOut(capture);
            command.execute(game);
        } finally {
            System.setOut(originalOut);
            capture.close();
        }
        String output = buffer.toString().trim();
        return output.isEmpty() ? "Command executed." : output;
    }

    private void syncStateFromGame() {
        GameSnapshot snapshot = game.createSnapshot();
        GameState state = new GameState();
        state.setPlayerName(game.getPlayer().getName());
        state.setCurrentRoomName(snapshot.getCurrentRoomName());
        state.setRoomDescription(game.getCurrentRoom().getLongDescription());
        state.setScore(snapshot.getScore());
        state.setHealth(snapshot.getHealth());
        state.setCurrentWeight(snapshot.getCurrentWeight());
        state.setMaxWeight(snapshot.getMaxWeight());
        state.setInventoryItems(itemNames(snapshot.getInventoryItems()));
        state.setRoomItems(itemNames(game.getCurrentRoom().getItems()));
        state.setAvailableExits(game.getCurrentRoom().getExitDirections());
        state.setNpcs(game.getCurrentRoom().getNpcNames());
        state.setVictory(game.isVictory());
        state.setQuestSummary(game.formatQuestProgress());
        state.setLoggedIn(game.isLoggedIn());
        this.currentState = state;
    }

    private List<String> itemNames(List<Item> items) {
        List<String> names = new ArrayList<>();
        if (items == null) {
            return names;
        }
        for (Item item : items) {
            names.add(item.getDescription() + " (" + item.getWeight() + ")");
        }
        return names;
    }

    private static class ParsedCommand {
        private final String word;
        private final String secondWord;

        private ParsedCommand(String word, String secondWord) {
            this.word = word;
            this.secondWord = secondWord;
        }
    }
}
