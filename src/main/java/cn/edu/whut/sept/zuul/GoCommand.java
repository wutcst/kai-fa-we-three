package cn.edu.whut.sept.zuul;

public class GoCommand extends Command
{
    @Override
    public boolean execute(Game game)
    {
        if(!hasSecondWord()) {
            System.out.println("go where?");
            return false;
        }

        String dir = getSecondWord();
        Room nextRoom = game.getCurrentRoom().getExit(dir);

        if(nextRoom == null) {
            System.out.println("No way!");
        } else {
            // 保存当前房间到历史
            game.pushCurrentRoomToHistory();

            game.setCurrentRoom(nextRoom);
            System.out.println(game.getCurrentRoom().getLongDescription());
        }

        return false;
    }
}
