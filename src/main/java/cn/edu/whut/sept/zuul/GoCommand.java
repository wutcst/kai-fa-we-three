package cn.edu.whut.sept.zuul;

public class GoCommand extends Command {
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Go where?");
            return false;
        }

        String direction = getSecondWord();
        Room currentRoom = game.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
            game.setHp(game.getHp() - 5); // 任务2：移动失败扣5HP
            System.out.println("移动失败，生命值-5，当前HP：" + game.getHp());
        } else {
            if (nextRoom instanceof TeleportRoom) {
                System.out.println("You step into the teleport chamber...");
                nextRoom = nextRoom.getExit(direction);
                if (nextRoom == null) {
                    System.out.println("The teleport fails!");
                    game.setHp(game.getHp() - 10); // 任务2：传送失败扣10HP
                    System.out.println("传送失败，生命值-10，当前HP：" + game.getHp());
                    return false;
                }
                System.out.println("You are teleported away!");
            }
            game.moveToRoom(nextRoom); // 调用改造后的移动逻辑（压栈）
            System.out.println(nextRoom.getLongDescription());
            game.setHp(game.getHp() + 1); // 任务2：成功移动+1HP
            System.out.println("移动成功，生命值+1，当前HP：" + game.getHp());
        }
        return false;
    }
}