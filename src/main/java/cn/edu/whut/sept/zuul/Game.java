package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.sql.SQLException;

public class Game {
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> roomHistory;
    private Player player;
    private PlayerRecord loggedInProfile;
    private final PlayerRepository playerRepository;
    private final SaveService saveService;
    private final SaveRepository saveRepository;
    private final DatabaseManager databaseManager;
    private final Map<String, Room> roomsByDescription = new HashMap<>();
    private String startRoomDescription;
    private int hp = GameConstants.INITIAL_HP;
    private int score = GameConstants.INITIAL_SCORE;
    private boolean victory = false;
    private final Map<String, String> questProgress = new HashMap<>();

    public Game(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.playerRepository = new PlayerRepository(databaseManager);
        this.saveRepository = new SaveRepository(databaseManager);
        LeaderboardRepository leaderboardRepository =
                new LeaderboardRepository(databaseManager);
        this.saveService = new SaveService(saveRepository,
                leaderboardRepository);
        try {
            WorldDataRepository worldDataRepository = new WorldDataRepository(databaseManager);
            worldDataRepository.seedDefaultWorldIfEmpty();
            WorldLoadResult world = worldDataRepository.loadWorld();
            roomsByDescription.putAll(world.getRoomsByDescription());
            currentRoom = world.getStartRoom();
            startRoomDescription = currentRoom.getShortDescription();
            populateNPCs();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load game world from database.", e);
        }
        parser = new Parser(playerRepository, saveService);
        roomHistory = new Stack<>();
        player = new Player(GameConstants.DEFAULT_PLAYER_NAME,
                (int) GameConstants.DEFAULT_MAX_WEIGHT);
        initializeQuestProgress();
    }

    /**
     * 初始化任务进度为起始状态。
     */
    private void initializeQuestProgress()
    {
        questProgress.clear();
        questProgress.put(GameConstants.QUEST_MAIN, GameConstants.QUEST_STARTED);
        questProgress.put(GameConstants.QUEST_SIDE_COOKIE,
                GameConstants.QUEST_NOT_STARTED);
    }


    private void populateNPCs()
    {
        Room outside = findRoomByDescription("outside the main entrance of the university");
        Room theater = findRoomByDescription("in a lecture theater");
        Room pub = findRoomByDescription("in the campus pub");
        Room lab = findRoomByDescription("in a computing lab");
        Room office = findRoomByDescription("in the computing admin office");
        Room teleport = findRoomByDescription("in a mysterious teleport chamber");

        if (outside != null) {
            NPC guard = new NPC("guard",
                    "欢迎来到武理校园！听说计算实验室里丢失了重要资料(task_item)，"
                            + "找到它并带回正门就算完成任务。向南进入实验室看看吧。");
            guard.addDialogue("has_task_item",
                    "你找到了 task_item！快把它放进门口的提交箱（drop task_item）来完成任务吧！"
                            + "这是你的最后一步了。");
            guard.addDialogue("main_completed",
                    "恭喜你完成了冒险！task_item 已安全归还，校园网络系统得救了。"
                            + "你是武理的骄傲，冒险者！");
            outside.addNPC(guard);
        }
        if (theater != null) {
            NPC lecturer = new NPC("lecturer",
                    "上课请保持安静。有人看到实验室的门昨晚没关好吗？"
                            + "管理员 office 那边也许有更多线索。");
            lecturer.addDialogue("hint_received",
                    "看来你已经和实验室的学生聊过了？去 office 找管理员吧，"
                            + "她应该有 keycard，也许能帮到你。");
            lecturer.addDialogue("main_completed",
                    "听说你已经完成了任务？很好！希望这次冒险让你学到了课堂之外的东西。");
            theater.addNPC(lecturer);
        }
        if (pub != null) {
            NPC bartender = new NPC("bartender",
                    "Welcome to the campus pub! 需要力量就试试 magic cookie，"
                            + "它能永久提升你的负重上限。east 方向可以回到正门。");
            bartender.addDialogue("has_cookie",
                    "你已经拿到了 magic cookie！别犹豫，eat cookie 吃下去，"
                            + "负重上限会永久提升 10kg，这是酒吧特供。");
            bartender.addDialogue("ate_cookie",
                    "你已经吃过 magic cookie 了？负重提升的感觉不错吧！"
                            + "现在你可以携带更多物品了，继续冒险吧。");
            bartender.addDialogue("main_completed",
                    "任务完成了？干得漂亮！下次来酒吧，我请你喝一杯特调能量饮料。");
            pub.addNPC(bartender);
        }
        if (lab != null) {
            NPC student = new NPC("student",
                    "我在找 task_item，那是提交实训报告的关键文件！"
                            + "拿到后记得带回 outside 才算真正完成使命。");
            student.addDialogue("has_task_item",
                    "你拿到 task_item 了！太好了！快把它带回 outside 门口的提交箱。"
                            + "路上小心，别走错方向浪费生命值。");
            student.addDialogue("main_completed",
                    "太棒了！多亏你找到了 task_item，我们的实训报告才能按时提交。"
                            + "你拯救了整个实验室的项目！");
            lab.addNPC(student);
        }
        if (office != null) {
            NPC admin = new NPC("admin",
                    "Please keep the computing lab tidy. "
                            + "Office hours are 9 to 5. 如需存档，请先 login 登录，再使用 save 命令。");
            admin.addDialogue("hint_received",
                    "你来问实验室的事？确实，昨晚门没锁好。"
                            + "keycard 就在这个房间里，拿上它去 lab 吧，也许还有别的发现。");
            admin.addDialogue("main_completed",
                    "感谢你帮实验室找回了重要文件。"
                            + "你的实训报告已经通过审核，祝你学业顺利！");
            office.addNPC(admin);
        }
        if (teleport != null) {
            NPC oracle = new NPC("oracle",
                    "进入此 chamber 者，将被随机传送到校园某处。"
                            + "祝你好运，冒险者。");
            oracle.addDialogue("main_completed",
                    "命运之线已经编织完成。你是被选中的人，"
                            + "传送之力将永远与你同在。");
            teleport.addNPC(oracle);
        }
    }

    public Room findRoomByDescription(String roomDescription)
    {
        return roomsByDescription.get(roomDescription);
    }

    public GameSnapshot createSnapshot()
    {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.setCurrentRoomName(currentRoom.getShortDescription());
        snapshot.setScore(score);
        snapshot.setHealth(hp);
        snapshot.setMaxWeight(player.getMaxWeight());
        snapshot.setCurrentWeight(player.getCurrentWeight());
        snapshot.setVictory(victory);
        snapshot.setInventoryItems(copyInventoryItems(player.getInventoryItems()));
        snapshot.setRoomItems(collectRoomItemSnapshots());
        snapshot.setQuestProgress(new HashMap<>(questProgress));
        return snapshot;
    }

    public void applySnapshot(GameSaveRecord saveRecord) throws SaveException
    {
        Room targetRoom = findRoomByDescription(saveRecord.getCurrentRoomName());
        if (targetRoom == null) {
            throw new SaveException("存档中的房间不存在：" + saveRecord.getCurrentRoomName());
        }

        restoreRoomItems(saveRecord.getRoomItems());

        this.currentRoom = targetRoom;
        this.roomHistory.clear();
        this.hp = saveRecord.getHealth();
        this.score = saveRecord.getScore();
        this.victory = saveRecord.isVictory();
        this.questProgress.clear();
        if (saveRecord.getQuestProgress().isEmpty()) {
            initializeQuestProgress();
        } else {
            this.questProgress.putAll(saveRecord.getQuestProgress());
        }
        player.replaceInventory(
                copyInventoryItems(saveRecord.getInventoryItems()),
                saveRecord.getMaxWeight(),
                saveRecord.getCurrentWeight()
        );
    }

    private List<RoomItemSnapshot> collectRoomItemSnapshots()
    {
        List<RoomItemSnapshot> roomItems = new ArrayList<>();
        for (Room room : roomsByDescription.values()) {
            for (Item item : room.getItems()) {
                roomItems.add(new RoomItemSnapshot(
                        room.getShortDescription(),
                        item.getDescription(),
                        item.getWeight()
                ));
            }
        }
        return roomItems;
    }

    private void restoreRoomItems(List<RoomItemSnapshot> roomItems)
    {
        for (Room room : roomsByDescription.values()) {
            room.clearItems();
        }

        if (roomItems == null) {
            return;
        }

        for (RoomItemSnapshot roomItem : roomItems) {
            Room room = findRoomByDescription(roomItem.getRoomName());
            if (room != null) {
                room.addItem(new Item(roomItem.getItemName(), roomItem.getWeight()));
            }
        }
    }

    public void updateQuestProgress(String questKey, String progressValue)
    {
        questProgress.put(questKey, progressValue);
    }

    public String getQuestProgressValue(String questKey)
    {
        return questProgress.get(questKey);
    }

    public void addScore(int points)
    {
        this.score += points;
    }

    private List<Item> copyInventoryItems(List<Item> items)
    {
        List<Item> copiedItems = new ArrayList<>();
        for (Item item : items) {
            copiedItems.add(new Item(item.getDescription(), item.getWeight()));
        }
        return copiedItems;
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            // 任务3：每次循环检查胜利条件
            if (checkVictory()) {
                System.out.println("=== 恭喜你！你完成了任务，游戏胜利！ ===");
                System.out.println("剧情文本：你带着 task_item 回到了大学正门，成功拯救了校园网络系统！");
                System.out.println("最终分数：" + score + "，最终生命值：" + hp);
                finished = true;
                continue;
            }

            Command command = parser.getCommand();
            if (command == null) {
                System.out.println("I don't understand...");
            } else {
                finished = command.execute(this);
            }

            // 任务2：如果HP为0，游戏结束
            if (hp <= 0) {
                System.out.println("你的生命值耗尽，游戏结束！");
                finished = true;
            }
        }
        System.out.println("Thank you for playing. Good bye.");
    }

    // 任务1：弹出上一个房间
    public Room popLastRoom() {
        if (roomHistory.isEmpty()) {
            return null;
        }
        return roomHistory.pop();
    }

    // 任务1：更新GoCommand逻辑（移动时压栈）
    public void moveToRoom(Room nextRoom) {
        if (currentRoom != nextRoom) {
            roomHistory.push(currentRoom); // 压入当前房间到栈
            currentRoom = nextRoom;
        }
    }

    // 胜利条件：必须将 task_item 放入 outside 的提交箱中
    public boolean checkVictory() {
        if (victory) {
            return true;
        }

        Room startRoom = findRoomByDescription(startRoomDescription);
        if (startRoom == null) {
            return false;
        }

        // 只有当 task_item 被放入了 outside 房间（提交箱）才触发胜利
        if (hasItemInRoom(startRoom, "task_item")) {
            victory = true;
            questProgress.put(GameConstants.QUEST_MAIN,
                    GameConstants.QUEST_COMPLETED);
            System.out.println("=== 恭喜！task_item 已成功提交！ ===");
            return true;
        }
        return false;
    }

    private boolean hasItemInRoom(Room room, String itemName) {
        for (Item item : room.getItems()) {
            if (item.getDescription().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasItemInInventory(String itemName)
    {
        for (Item item : player.getInventoryItems()) {
            if (item.getDescription().equals(itemName)) {
                return true;
            }
        }
        return false;
    }


    public Player getPlayer() {
        return player;
    }

    public PlayerRecord getLoggedInProfile() {
        return loggedInProfile;
    }

    public boolean isLoggedIn() {
        return loggedInProfile != null;
    }

    public void applyPlayerLogin(PlayerLoginResult loginResult) {
        this.loggedInProfile = loginResult.getPlayer();
        this.player.setName(loginResult.getPlayer().getName());

        if (loginResult.isNewlyCreated()) {
            // 新用户：直接重置到初始世界
            resetWorld();
            System.out.println("欢迎新玩家 " + loginResult.getPlayer().getName()
                    + "！玩家档案已创建并登录成功。");
        } else {
            // 老用户：先重置世界，再尝试自动加载最近存档
            resetWorld();
            boolean loaded = tryAutoLoadLatestSave();
            if (loaded) {
                System.out.println("欢迎回来，" + loginResult.getPlayer().getName()
                        + "！已自动加载你的最近存档。");
            } else {
                System.out.println("欢迎回来，" + loginResult.getPlayer().getName()
                        + "！你还没有存档，开始新的冒险吧。");
            }
        }
        System.out.println("玩家 ID：" + loginResult.getPlayer().getId());
    }

    /**
     * 尝试加载当前登录用户最近的一个存档。
     *
     * @return true 表示成功加载存档，false 表示没有可用存档
     */
    private boolean tryAutoLoadLatestSave() {
        try {
            List<GameSaveRecord> saves = saveRepository.listSaves(loggedInProfile.getId());
            if (saves == null || saves.isEmpty()) {
                return false;
            }
            // listSaves 按 saved_at DESC 排序，第一个即为最近存档
            GameSaveRecord latestSave = saveRepository.loadGame(
                    loggedInProfile.getId(), saves.get(0).getSaveName());
            if (latestSave != null) {
                applySnapshot(latestSave);
                return true;
            }
        } catch (Exception e) {
            // 加载失败则保持初始世界状态
        }
        return false;
    }

    public void resetWorld() {
        try {
            WorldDataRepository worldDataRepository = new WorldDataRepository(databaseManager);
            worldDataRepository.seedDefaultWorldIfEmpty();
            WorldLoadResult world = worldDataRepository.loadWorld();
            roomsByDescription.clear();
            roomsByDescription.putAll(world.getRoomsByDescription());
            currentRoom = world.getStartRoom();
            startRoomDescription = currentRoom.getShortDescription();
            populateNPCs();
        } catch (SQLException e) {
            System.err.println("Failed to reload world: " + e.getMessage());
        }
        roomHistory.clear();
        hp = GameConstants.INITIAL_HP;
        score = GameConstants.INITIAL_SCORE;
        victory = false;
        player.clearInventory();
        player.setMaxWeight((int) GameConstants.DEFAULT_MAX_WEIGHT);
        initializeQuestProgress();
    }

    public SaveService getSaveService() {
        return saveService;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }


    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("=== 校园冒险任务 ===");
        System.out.println("目标：前往 computing lab 找到 task_item，再带回正门(outside) 完成使命。");
        System.out.println("支线：在 pub 找到 cookie 并 eat cookie，可提升负重上限。");
        System.out.println("提示：使用 talk <NPC名> 与 NPC 对话获取线索。");
        System.out.println();
        System.out.println("Type 'help' if you need help.");
        System.out.println("Type 'login <username>' to create or load your player profile.");
        System.out.println("Type 'save/load/saves/delete-save <saveName>' to manage game saves.");
        System.out.println("当前生命值：" + hp + "，当前分数：" + score);
        System.out.println("任务进度：" + formatQuestProgress());
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    public String formatQuestProgress()
    {
        return "主线=" + questProgress.getOrDefault("main_quest", "unknown")
                + "，支线(cookie)=" + questProgress.getOrDefault("side_quest_cookie", "unknown");
    }
}
