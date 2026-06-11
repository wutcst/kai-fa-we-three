package cn.edu.whut.sept.zuul;

// 1. 导入 Stack
import java.util.Stack;

public class Game
{
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> roomHistory;

    // 记录游戏初始房间（出生点）
    private final Room startRoom;
    // 定义胜利所需物品名称
    private static final String WIN_ITEM = "treasure";

    // 玩家对象（管理生命值、背包、负重）
    private Player player;

    public Game()
    {
        createRooms();
        parser = new Parser();
        roomHistory = new Stack<>();
        // 保存初始房间
        startRoom = currentRoom;
        // 初始化玩家：姓名+初始最大负重
        player = new Player("Adventurer", 50);
    }

    private void createRooms()
    {
        Room outside, theater, pub, lab, office;

        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");

        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theater.setExit("west", outside);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        office.setExit("west", lab);

        currentRoom = outside;

        // 测试：给办公室放入胜利物品 treasure，方便测试通关
        office.addItem(new Item("treasure", 10));
    }

    public void play()
    {
        printWelcome();

        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            if(command == null) {
                System.out.println("I don't understand...");
            } else {
                finished = command.execute(this);
            }
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
        // 开局展示玩家初始生命值
        System.out.println("你的初始生命值：" + player.getCurrentHp() + "/" + player.getMaxHp());
        System.out.println("【任务提示】找到宝藏(treasure)并带回初始房间即可获胜！");
        System.out.println();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room){
        this.currentRoom = room;
    }

    /*
     * 实现 back 命令：回到上一个房间
     */
    public void back() {
        // 如果栈为空，说明已经在起点
        if (roomHistory.isEmpty()) {
            System.out.println("你已经回到起点了，无法继续后退！");
            return;
        }

        // 弹出上一个房间，并切换过去
        currentRoom = roomHistory.pop();
        System.out.println("你回到了上一个房间。");
        System.out.println(currentRoom.getLongDescription());

        // 后退房间后，也检测胜利条件
        checkWinCondition();
    }

    /**
     * 玩家移动时，把当前房间存入历史栈（给 GoCommand 调用）
     */
    public void pushCurrentRoomToHistory() {
        roomHistory.push(currentRoom);
    }

    // ========= 新增：胜利条件检测核心方法 =========
    public boolean checkWinCondition(){
        // 条件1：当前房间 == 初始房间
        // 条件2：玩家背包拥有胜利物品 treasure
        if(currentRoom == startRoom && player.hasItem(WIN_ITEM)){
            // 打印胜利剧情文本
            System.out.println("========================================");
            System.out.println(" 恭喜你！任务完成，游戏胜利！");
            System.out.println("你成功找到宝藏并平安带回起点，成为了伟大的冒险家！");
            System.out.println("========================================");
            return true;
        }
        return false;
    }

    // ========= 新增对外方法：供所有指令类获取玩家对象 =========
    public Player getPlayer() {
        return player;
    }

    // 新增：给 look 指令获取当前房间描述
    public String getRoomInfo() {
        return currentRoom.getLongDescription();
    }
}