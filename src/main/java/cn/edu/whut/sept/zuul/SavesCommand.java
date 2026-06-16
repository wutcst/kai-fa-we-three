package cn.edu.whut.sept.zuul;

public class SavesCommand extends Command
{
    private final SaveService saveService;

    public SavesCommand(SaveService saveService)
    {
        this.saveService = saveService;
    }

    @Override
    public boolean execute(Game game)
    {
        try {
            System.out.println(saveService.listSaves(game));
        } catch (SaveException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
