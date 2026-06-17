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
            game.setHp(game.getHp() - 5);
            System.out.println("移动失败，生命值-5，当前HP：" + game.getHp());
        } else {
            if (currentRoom instanceof TeleportRoom) {
                // 传送室：getExit 已返回随机目标，添加提示
                System.out.println("🌀 传送室启动！你被随机传送了！");
            }
            game.moveToRoom(nextRoom);
            System.out.println(nextRoom.getLongDescription());
            game.setHp(game.getHp() + 1);
            System.out.println("移动成功，生命值+1，当前HP：" + game.getHp());
        }
        return false;
    }
}