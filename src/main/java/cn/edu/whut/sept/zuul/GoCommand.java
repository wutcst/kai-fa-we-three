package cn.edu.whut.sept.zuul;

public class GoCommand extends Command
{
    public boolean execute(Game game)
    {
        if(!hasSecondWord()) {
            System.out.println("Go where?");
        }

        String direction = getSecondWord();
        Room currentRoom = game.getCurrentRoom();

        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            if (nextRoom instanceof TeleportRoom) {
                System.out.println("You step into the teleport chamber...");
                nextRoom = nextRoom.getExit(direction);
                if (nextRoom == null) {
                    System.out.println("The teleport fails!");
                    return false;
                }
                System.out.println("You are teleported away!");
            }
            game.setCurrentRoom(nextRoom);
            System.out.println(nextRoom.getLongDescription());
        }

        return false;
    }
}
