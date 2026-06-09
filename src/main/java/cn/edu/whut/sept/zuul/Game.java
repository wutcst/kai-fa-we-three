package cn.edu.whut.sept.zuul;

// 1. 必须导入 Stack
import java.util.Stack;

public class Game
{
    private Parser parser;
    private Room currentRoom;

    // 2. 添加栈，用来记录走过的房间
    private Stack<Room> roomHistory;

    public Game()
    {
        createRooms();
        parser = new Parser();

        // 3. 初始化栈
        roomHistory = new Stack<>();
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
    }

    /**
     * 玩家移动时，把当前房间存入历史栈（给 GoCommand 调用）
     */
    public void pushCurrentRoomToHistory() {
        roomHistory.push(currentRoom);
    }
}