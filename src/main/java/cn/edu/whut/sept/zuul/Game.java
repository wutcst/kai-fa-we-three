package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Game {
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> roomHistory; // 房间历史栈
    private Player player; // 新增玩家实例
    private int hp = 100; // 任务2：生命值初始值
    private boolean victory = false; // 任务3：胜利标志

    public Game() {
        createRooms();
        parser = new Parser();
        roomHistory = new Stack<>(); // 初始化栈
        player = new Player("Adventurer", 10); // 初始化玩家（默认最大负重10）
    }


    private void createRooms() {
        Room outside, theater, pub, lab, office;
        TeleportRoom teleport;

        // 创建房间
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");

        List<Room> allRooms = new ArrayList<>();
        allRooms.add(outside);
        allRooms.add(theater);
        allRooms.add(pub);
        allRooms.add(lab);
        allRooms.add(office);

        teleport = new TeleportRoom("in a mysterious teleport chamber", allRooms);
        allRooms.add(teleport);

        // 初始化出口
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        theater.setExit("west", outside);
        theater.setExit("north", teleport);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        office.setExit("west", lab);


        pub.addNPC(new NPC("bartender", "Welcome to the campus pub! Take a break from your adventure."));
        office.addNPC(new NPC("admin", "Please keep the computing lab tidy. Office hours are 9 to 5."));

        // 添加胜利物品
        lab.addItem(new Item("task_item", 2));
        // 添加魔法饼干到pub
        pub.addItem(new Item("cookie", 1));

        currentRoom = outside; // 起点
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            // 任务3：每次循环检查胜利条件
            if (checkVictory()) {
                System.out.println("=== 恭喜你！你完成了任务，游戏胜利！ ===");
                System.out.println("剧情文本：你带着任务物品回到了起点，成功拯救了大学！");
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

    // 任务3：胜利条件检查（携带task_item回到初始房间outside）
    private boolean checkVictory() {
        Room startRoom = null;
        // 找到初始房间（outside）
        for (Room room : roomHistory) {
            if (room.getShortDescription().equals("outside the main entrance of the university")) {
                startRoom = room;
                break;
            }
        }
        if (startRoom == null) {
            startRoom = currentRoom; // 初始房间可能还没入栈
        }

        // 条件1：在初始房间；条件2：背包有task_item
        return currentRoom.equals(startRoom)
                && player.dropItem("task_item") != null; // 临时检查后放回
    }


    public Player getPlayer() {
        return player;
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
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println("当前生命值：" + hp);
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }
}