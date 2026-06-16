package cn.edu.whut.sept.zuul;

public class LoadCommand extends Command
{
    private final SaveService saveService;

    public LoadCommand(SaveService saveService)
    {
        this.saveService = saveService;
    }

    @Override
    public boolean execute(Game game)
    {
        if (!hasSecondWord()) {
            System.out.println("Load which save?");
            System.out.println("Usage: load <saveName>");
            return false;
        }

        try {
            System.out.println(saveService.load(game, getSecondWord()));
        } catch (SaveException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
