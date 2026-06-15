package cn.edu.whut.sept.zuul;

// 修复IO流找不到符号报错
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * GameService is the unified service layer of the game.
 * GUI, database and tests should interact with the game through this class.
 */
public class GameService {
    private GameState currentState;
    private final Game game;

    public GameService() {
        this.currentState = new GameState();
        this.game = new Game();
        syncGameState();
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
        // 拆分前端传入指令，主指令+第二个参数
        String[] cmdParts = command.split("\\s+", 2);
        String mainCmd = cmdParts[0];
        String secondWord = cmdParts.length > 1 ? cmdParts[1] : null;

        CommandWords cmdWords = new CommandWords();
        Command cmdObj = cmdWords.get(mainCmd);
        String message;

        // 未知指令处理
        if (cmdObj == null) {
            message = "未知指令！可用指令：go/back/take/drop/eat/look/help/items";
            return new CommandResult(false, message, currentState);
        }

        cmdObj.setSecondWord(secondWord);

        // 捕获控制台输出，返回给网页前端
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);

        try {
            cmdObj.execute(game);
            printStream.flush();
            message = outputStream.toString().replace("\n", "<br>");
        } finally {
            // 恢复标准输出流
            System.setOut(originalOut);
        }

        // 同步最新游戏数据到GameState
        syncGameState();

        // 追加游戏结束/胜利提示
        if (game.getHp() <= 0) {
            message += "<br>你的生命值耗尽，游戏结束！";
        }
        if (game.checkVictory()) {
            message += "<br>=== 恭喜你！你完成了任务，游戏胜利！ ===";
        }

        return new CommandResult(true, message, currentState);
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

    /**
     * 同步Game真实数据到GameState，适配网页展示
     * 已删除getScore、getInventory调用，消除对应报错
     */
    private void syncGameState() {
        Player player = game.getPlayer();
        Room room = game.getCurrentRoom();
        if (player == null || room == null) {
            return;
        }

        // 基础玩家、房间、血量信息
        currentState.setPlayerName(player.getName());
        currentState.setCurrentRoomName(room.getShortDescription());
        currentState.setHealth(game.getHp());

        // 负重信息（已给Player补充getter，无报错）
        currentState.setCurrentWeight(player.getCurrentWeight());
        currentState.setMaxWeight(player.getMaxWeight());

        // 背包物品：复用原有getInventoryString文本解析，不调用getInventory()
        currentState.getInventoryItems().clear();
        String invInfo = player.getInventoryString();
        String[] lines = invInfo.split("\n");
        for (String line : lines) {
            String trimLine = line.trim();
            if (trimLine.startsWith("-")) {
                String itemName = trimLine.replace("-", "").split(" ")[0].trim();
                currentState.getInventoryItems().add(itemName);
            }
        }

        // 清空房间物品列表（如需扩展再补充Room读取逻辑）
        currentState.getRoomItems().clear();
    }
}