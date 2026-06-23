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
    private boolean victory = false;
    private final Map<String, String> questProgress = new HashMap<>();
    private final QuestEngine questEngine = new QuestEngine();
    private final Map<String, Shop> shopsByRoom = new HashMap<>();
    private final WorldState worldState = new WorldState();
    private final java.util.Set<String> talkedNpcs = new java.util.HashSet<>();
    private final EndingCalculator.PlayerStats stats = new EndingCalculator.PlayerStats();
    private final CraftingManager craftingManager = new CraftingManager();
    /** NPC 亲密度：npcName → 对话次数 */
    private final Map<String, Integer> npcAffinity = new HashMap<>();
    private int teleportVisits = 0;
    private EndingType endingType = null;
    private int finalScore = 0;

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
        initQuests();
        initShops();
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
                    "👋 新同学你好！第一次来校园吧？\n"
                            + "📖 先捡起地上的【欢迎手册】看看游戏说明！\n"
                            + "🗺️ 再拿上【校园地图】，不然你会迷路的——没地图可走不出正门！\n"
                            + "拿完两样东西再来找我，我告诉你任务详情。");
            guard.addDialogue("has_map",
                    "很好，地图和手册都拿到了！听好你的任务：\n"
                            + "校园网络被攻击，实验室数据丢失。你需要：\n"
                            + "① 去办公室（正门▶东▶东）找管理员答题拿 keycard 和 reference\n"
                            + "② 去教室（办公室▼南）找老师答题拿 signature\n"
                            + "③ 去实验室（正门▼南）用 keycard 提取 code_data\n"
                            + "④ 在实验室 ⚗️合成台 合成 perfect_report\n"
                            + "⑤ 回到这里投进 📮提交箱 即可通关！\n"
                            + "💡 提示：管理员只在 9:00-17:00 上班，记得白天去。咖啡店有魔法饼干可以买，吃了能多带东西。");
            guard.addDialogue("has_task_item",
                    "你手里拿着 perfect_report 了！快在背包里选中它，然后点击 📮提交箱！");
            guard.addDialogue("main_completed",
                    "🎉 恭喜通关！perfect_report 已安全提交，校园网络系统恢复正常。");
            outside.addNPC(guard);
        }
        if (theater != null) {
            NPC lecturer = new NPC("lecturer",
                    "我是你的导师。想要我的 signature 签名？\n"
                            + "你得先通过我的考核——答对问题才能拿到签名，答错了可不行。\n"
                            + "答对有 🪙5金币 奖励。准备好了就点下面的按钮。");
            lecturer.addDialogue("hint_received",
                    "快去办公室拿 reference，去实验室拿 code_data，然后回来找我答题拿签名！");
            lecturer.addDialogue("main_completed",
                    "完美的报告！你是我带过最优秀的学生。");
            theater.addNPC(lecturer);
            theater.addItem(new Item("signature", 1, "lecturer"));
        }
        if (pub != null) {
            // 移除初始就有的饼干，必须从商店购买
            pub.removeItem("cookie");
            pub.removeItem("energy_drink");
            NPC barista = new NPC("barista",
                    "☕ 欢迎来到校园咖啡店！\n"
                            + "我这里有【魔法饼干 cookie】和【咖啡 coffee】，\n"
                            + "吃了各永久 +5kg 负重上限，两个都吃就是 +10kg！\n"
                            + "每样 10 金币，点击下方'打开商店'购买。\n"
                            + "金币可以通过回答老师和管理员的问题获得。");
            barista.addDialogue("has_cookie",
                    "手里有 cookie 就赶紧吃了吧！背包里点 🍽 吃掉，负重永久 +10kg。");
            barista.addDialogue("ate_cookie",
                    "负重提升了吧？现在可以同时拿更多任务物品了。继续你的冒险吧！");
            barista.addDialogue("main_completed",
                    "任务完成了！来，这杯咖啡我请！☕");
            pub.addNPC(barista);
        }
        if (lab != null) {
            NPC student = new NPC("student",
                    "💻 我在实验室通宵好几天了。地上那个就是【code_data】——"
                            + "但电脑有安全锁，需要【keycard】门禁卡才能提取！"
                            + "先去 ▶东边【办公室】找管理员。他人很好，但会考你一道题。");
            student.addDialogue("has_keycard",
                    "你有 keycard 了！快点击地上的 code_data 提取数据！"
                            + "注意：这里偶尔有失控的机器人出没，答对题就能赶走它。");
            student.addDialogue("has_task_item",
                    "三样材料齐了！去底部的 ⚗️合成台 合成 perfect_report！");
            student.addDialogue("main_completed",
                    "太棒了！我们的实训成绩保住了！");
            lab.addNPC(student);
            // code_data 需要 keycard 才能拿
            lab.addItem(new Item("code_data", 2, "keycard"));
        }
        if (office != null) {
            NPC admin = new NPC("admin",
                    "📋 我是实验室管理员（上班时间 9:00-17:00）。\n"
                            + "地上有【reference】可以直接拿。\n"
                            + "至于【keycard】门禁卡——得先通过我的考核。\n"
                            + "答对有 🪙5金币 奖励。准备好了就点下面的按钮。");
            admin.addDialogue("hint_received",
                    "答对了就能拿 keycard！拿上 reference 和 keycard，"
                            + "去 ▼南边【教室】找老师要签名。");
            admin.addDialogue("main_completed",
                    "感谢你拯救了实验室数据！");
            admin.addDialogue("off_duty",
                    "管理员已经下班了（9:00-17:00），明天再来吧。");
            office.addNPC(admin);
            office.addItem(new Item("reference", 2));
            // keycard 需要答对管理员的问题才能拿
            office.addItem(new Item("keycard", 1, "admin"));
        }
        if (teleport != null) {
            NPC oracle = new NPC("oracle",
                    "🌀 我是时空守护者，只在夜晚（18:00后）现身。\n"
                            + "暴风雨时传送之力达到巅峰——我可以把你送到关键房间！\n"
                            + "💫 如果你多次来找我（3次以上），我会告诉你一个隐藏的秘密…\n"
                            + "这个秘密可以让你获得特殊结局。");
            oracle.addDialogue("main_completed",
                    "命运的齿轮已经停转。你是被选中的人，冒险者。");
            oracle.addDialogue("secret_hint",
                    "你已经来了好几次了…听好：\n"
                            + "在提交 perfect_report 之前，多和我对话可以触发隐藏结局——「时空旅者」。\n"
                            + "这个结局的评分远高于普通通关。继续来找我吧。");
            oracle.addDialogue("not_time",
                    "我只能在夜晚（18:00后）出现…太阳落山后再来吧。");
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
        snapshot.setScore(getScore());
        snapshot.setHealth(getHp());
        snapshot.setMaxWeight(player.getMaxWeight());
        snapshot.setCurrentWeight(player.getCurrentWeight());
        snapshot.setLevel(player.getLevel());
        snapshot.setExp(player.getExp());
        snapshot.setAtk(player.getAtk());
        snapshot.setDef(player.getDef());
        snapshot.setSp(player.getSp());
        snapshot.setGold(player.getGold());
        snapshot.setGameTime(worldState.getGameTimeMinutes());
        snapshot.setDayCount(worldState.getDayCount());
        snapshot.setTeleportVisits(teleportVisits);
        snapshot.setVictory(victory);
        snapshot.setEndingTypeName(endingType != null ? endingType.name() : "");
        snapshot.setStepCount(stats.stepCount);
        snapshot.setItemsCollected(stats.itemsCollected);
        snapshot.setQuizTotal(stats.quizTotal);
        snapshot.setQuizCorrect(stats.quizCorrect);
        snapshot.setEnemiesDefeated(stats.enemiesDefeated);
        snapshot.setFleeCount(stats.fleeCount);
        snapshot.setRoomsVisited(stats.roomsVisited);
        snapshot.setNpcAffinityData(encodeNpcAffinity());
        snapshot.setNpcTalkedData(encodeNpcTalked());
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
        player.setHp(saveRecord.getHealth());
        player.setScore(saveRecord.getScore());
        this.victory = saveRecord.isVictory();
        this.questProgress.clear();
        if (saveRecord.getQuestProgress().isEmpty()) {
            initializeQuestProgress();
        } else {
            this.questProgress.putAll(saveRecord.getQuestProgress());
        }
        player.setLevel(saveRecord.getLevel());
        player.setExp(saveRecord.getExp());
        player.setAtk(saveRecord.getAtk());
        player.setDef(saveRecord.getDef());
        player.setSp(saveRecord.getSp());
        player.setGold(saveRecord.getGold());
        worldState.setGameTimeMinutes(saveRecord.getGameTime());
        worldState.setDayCount(saveRecord.getDayCount());
        this.teleportVisits = saveRecord.getTeleportVisits();
        if (!saveRecord.getEndingTypeName().isEmpty()) {
            try { this.endingType = EndingType.valueOf(saveRecord.getEndingTypeName()); } catch (IllegalArgumentException e) {}
        }
        this.stats.stepCount = saveRecord.getStepCount();
        this.stats.itemsCollected = saveRecord.getItemsCollected();
        this.stats.quizTotal = saveRecord.getQuizTotal();
        this.stats.quizCorrect = saveRecord.getQuizCorrect();
        this.stats.enemiesDefeated = saveRecord.getEnemiesDefeated();
        this.stats.fleeCount = saveRecord.getFleeCount();
        this.stats.roomsVisited = saveRecord.getRoomsVisited();
        decodeNpcAffinity(saveRecord.getNpcAffinityData());
        decodeNpcTalked(saveRecord.getNpcTalkedData());
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

    public Map<String, String> getQuestProgressMap() {
        return questProgress;
    }

    public void addScore(int points)
    {
        player.addScore(points);
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
                System.out.println("剧情文本：你带着 perfect_report 回到了大学正门，成功拯救了校园网络系统！");
                System.out.println("最终分数：" + getScore() + "，最终生命值：" + getHp());
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
            if (getHp() <= 0) {
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

    // 胜利条件：必须将 perfect_report 放入 outside 的提交箱中
    public boolean checkVictory() {
        if (victory) {
            return true;
        }

        Room startRoom = findRoomByDescription(startRoomDescription);
        if (startRoom == null) {
            return false;
        }

        // 只有当 perfect_report 被放入了 outside 房间（提交箱）才触发胜利
        if (hasItemInRoom(startRoom, "perfect_report")) {
            victory = true;
            calculateEnding();
            questProgress.put(GameConstants.QUEST_MAIN,
                    GameConstants.QUEST_COMPLETED);
            System.out.println("=== 恭喜！perfect_report 已成功提交！ ===");
            if (endingType != null) {
                System.out.println("🏆 结局：" + player.getLevelTitle() + " · " + endingType.getTitle());
                System.out.println("📝 " + endingType.getDescription());
                System.out.println("⭐ 最终评分：" + finalScore);
            }
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

    public boolean hasItemInInventory(String itemName)
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

    public void logout() {
        this.loggedInProfile = null;
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
        player.setHp(GameConstants.INITIAL_HP);
        player.setScore(GameConstants.INITIAL_SCORE);
        victory = false;
        player.clearInventory();
        player.setMaxWeight((int) GameConstants.DEFAULT_MAX_WEIGHT);
        player.setLevel(1);
        player.setExp(0);
        player.setAtk(10);
        player.setDef(5);
        player.setSp(50);
        player.setGold(GameConstants.INITIAL_GOLD);
        initializeQuestProgress();
        initQuests();
        initShops();
        talkedNpcs.clear();
        npcAffinity.clear();
        teleportVisits = 0;
        stats.stepCount = 0; stats.quizTotal = 0; stats.quizCorrect = 0;
        stats.enemiesDefeated = 0; stats.fleeCount = 0;
        stats.itemsCollected = 0; stats.roomsVisited = 0;
        endingType = null; finalScore = 0;
    }

    public SaveService getSaveService() {
        return saveService;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public int getScore() {
        return player.getScore();
    }

    public void setScore(int score) {
        player.setScore(score);
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public int getHp() {
        return player.getHp();
    }

    public void setHp(int hp) {
        player.setHp(hp);
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
        System.out.println("目标：收集 [code_data], [reference], 解答导师问题获取 [signature]，使用 combine 合成 [perfect_report]，带回正门(outside)完成使命。");
        System.out.println("支线：在咖啡店找到 cookie 并 eat cookie，可提升负重上限。");
        System.out.println("提示：使用 talk <NPC名> 与 NPC 对话获取线索，使用 answer A/B 答题。");
        System.out.println();
        System.out.println("Type 'help' if you need help.");
        System.out.println("Type 'login <username>' to create or load your player profile.");
        System.out.println("Type 'save/load/saves/delete-save <saveName>' to manage game saves.");
        System.out.println("当前生命值：" + getHp() + "，当前分数：" + getScore());
        System.out.println("任务进度：" + formatQuestProgress());
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    public String formatQuestProgress()
    {
        return "主线=" + questProgress.getOrDefault("main_quest", "unknown")
                + "，支线(cookie)=" + questProgress.getOrDefault("side_quest_cookie", "unknown");
    }

    // ========== 战斗系统 ==========


    // ========== 任务系统 ==========

    public QuestEngine getQuestEngine() {
        return questEngine;
    }

    /**
     * 初始化所有任务模板并注册到任务引擎。
     */
    private void initQuests() {
        // === 主线：合成并提交完美实验报告 ===
        Quest mainQuest = new Quest("main_quest", "完美实验报告",
                "收集材料、答题获取签名、合成 perfect_report 并提交到正门",
                Quest.QuestType.MAIN);

        QuestStage mainStage1 = new QuestStage(0, "收集三样关键材料");
        mainStage1.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.COLLECT, "code_data", 1));
        mainStage1.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.COLLECT, "reference", 1));
        mainStage1.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.ANSWER, "lecturer", 1));
        mainStage1.setStageReward(new QuestReward(50, 20));

        QuestStage mainStage2 = new QuestStage(1, "合成 perfect_report 并提交");
        mainStage2.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.COMBINE, "perfect_report", 1));
        mainStage2.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "outside the main entrance of the university", 1));
        mainStage2.setStageReward(new QuestReward(100, 50));
        mainQuest.setFinalReward(new QuestReward(200, 100));

        mainQuest.getStages().add(mainStage1);
        mainQuest.getStages().add(mainStage2);
        questEngine.registerQuest(mainQuest);

        // === 支线：吃魔法饼干 ===
        Quest cookieQuest = new Quest("side_cookie", "魔法饼干的秘密",
                "在咖啡店找到魔法饼干并吃掉它，永久提升负重上限",
                Quest.QuestType.SIDE);
        QuestStage cookieStage = new QuestStage(0, "找到并吃掉魔法饼干");
        cookieStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.COLLECT, "cookie", 1));
        cookieStage.setStageReward(new QuestReward(30, 10));
        cookieQuest.setFinalReward(new QuestReward(50, 0));
        cookieQuest.getStages().add(cookieStage);
        questEngine.registerQuest(cookieQuest);

        // === 支线：探索所有房间 ===
        Quest exploreQuest = new Quest("explorer", "校园探索者",
                "探索校园的每一个角落",
                Quest.QuestType.SIDE);
        QuestStage exploreStage = new QuestStage(0, "探索所有 6 个房间");
        exploreStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "in a lecture theater", 1));
        exploreStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "in the campus pub", 1));
        exploreStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "in a computing lab", 1));
        exploreStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "in the computing admin office", 1));
        exploreStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.EXPLORE, "in a mysterious teleport chamber", 1));
        exploreStage.setStageReward(new QuestReward(60, 30));
        exploreQuest.setFinalReward(new QuestReward(100, 50));
        exploreQuest.getStages().add(exploreStage);
        questEngine.registerQuest(exploreQuest);

        // === 隐藏任务：与 Oracle 对话 ===
        Quest oracleQuest = new Quest("oracle_secret", "时空的秘密",
                "在传送室与 Oracle 深入对话，揭开校园的隐藏真相",
                Quest.QuestType.HIDDEN);
        oracleQuest.setHidden(true);
        QuestStage oracleStage = new QuestStage(0, "与 Oracle 对话 3 次");
        oracleStage.getObjectives().add(new QuestObjective(
                QuestObjective.ObjectiveType.TALK, "oracle", 3));
        oracleStage.setStageReward(new QuestReward(80, 50));
        oracleQuest.setFinalReward(new QuestReward(200, 100));
        oracleQuest.getStages().add(oracleStage);
        questEngine.registerQuest(oracleQuest);

        // 启动所有非隐藏任务
        questEngine.startQuest("main_quest", questProgress);
        questEngine.startQuest("side_cookie", questProgress);
        questEngine.startQuest("explorer", questProgress);
    }

    // ========== 商店系统 ==========

    /**
     * 初始化商店并将商店关联到 NPC。
     */
    private void initShops() {
        shopsByRoom.clear();

        // 咖啡店: 饼干+5kg负重，咖啡+5kg负重
        Shop pubShop = new Shop("barista");
        pubShop.addItem(new ShopItem("cookie", 10, ShopItem.ItemType.CONSUMABLE)
                .withStat("weight", 5));
        pubShop.addItem(new ShopItem("coffee", 10, ShopItem.ItemType.CONSUMABLE)
                .withStat("weight", 5));
        shopsByRoom.put("in the campus pub", pubShop);

        // 办公室: 管理员不卖武器了，只提供线索
        // (不再设置 office shop)

        // 将商店关联到 NPC
        for (Room room : roomsByDescription.values()) {
            Shop shop = shopsByRoom.get(room.getShortDescription());
            if (shop != null) {
                for (String npcName : room.getNpcNames()) {
                    NPC npc = room.getNpc(npcName);
                    if (npc != null && shop.getMerchantNpcName().equals(npcName)) {
                        npc.setShop(shop);
                    }
                }
            }
        }
    }

    /**
     * 获取指定房间的商店，没有则返回 null。
     */
    public Shop getShopForRoom(String roomDesc) {
        return shopsByRoom.get(roomDesc);
    }

    public WorldState getWorldState() {
        return worldState;
    }

    /** 标记 NPC 已被对话过，返回是否是第一次 */
    public boolean markNpcTalked(String npcName) {
        return talkedNpcs.add(npcName.toLowerCase());
    }

    // ========== 统计数据 & 结局 ==========

    public EndingCalculator.PlayerStats getStats() { return stats; }
    public CraftingManager getCraftingManager() { return craftingManager; }
    public EndingType getEndingType() { return endingType; }
    public int getFinalScore() { return finalScore; }

    public void addStep() { stats.stepCount++; }
    public void addQuizAttempt(boolean correct) {
        stats.quizTotal++;
        if (correct) stats.quizCorrect++;
    }
    public void addItemCollected() { stats.itemsCollected++; }
    public void addRoomVisited() { stats.roomsVisited++; }

    /** 计算并设置结局 */
    public void calculateEnding() {
        if (endingType != null) return;
        endingType = EndingCalculator.calculate(this, stats);
        finalScore = EndingCalculator.calculateScore(this, stats, endingType);
    }

    // ========== NPC 好感度 ==========

    /** 记录一次 NPC 对话，返回当前好感等级 (1-4) */
    public int recordNpcAffinity(String npcName) {
        String key = npcName.toLowerCase();
        int count = npcAffinity.getOrDefault(key, 0) + 1;
        npcAffinity.put(key, count);
        if (count >= 4) return 4;
        if (count >= 3) return 3;
        if (count >= 2) return 2;
        return 1;
    }

    public int getNpcAffinity(String npcName) {
        return npcAffinity.getOrDefault(npcName.toLowerCase(), 0);
    }

    private String encodeNpcAffinity() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : npcAffinity.entrySet()) {
            if (sb.length() > 0) sb.append(",");
            sb.append(e.getKey()).append(":").append(e.getValue());
        }
        return sb.toString();
    }

    private void decodeNpcAffinity(String data) {
        npcAffinity.clear();
        if (data == null || data.isEmpty()) return;
        for (String part : data.split(",")) {
            String[] kv = part.split(":");
            if (kv.length == 2) {
                try { npcAffinity.put(kv[0], Integer.parseInt(kv[1])); } catch (NumberFormatException e) {}
            }
        }
    }

    private String encodeNpcTalked() {
        StringBuilder sb = new StringBuilder();
        for (String name : talkedNpcs) {
            if (sb.length() > 0) sb.append(",");
            sb.append(name);
        }
        return sb.toString();
    }

    private void decodeNpcTalked(String data) {
        talkedNpcs.clear();
        if (data == null || data.isEmpty()) return;
        for (String name : data.split(",")) {
            if (!name.isEmpty()) talkedNpcs.add(name);
        }
    }

    public int getTeleportVisits() { return teleportVisits; }

    public void incrementTeleportVisits() {
        teleportVisits++;
        if (teleportVisits == 3) {
            System.out.println("🌀 你已经是第三次进入传送室了…时空的力量在你体内涌动。");
        }
    }

}
