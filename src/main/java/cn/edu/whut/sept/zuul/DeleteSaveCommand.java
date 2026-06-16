package cn.edu.whut.sept.zuul;

public class DeleteSaveCommand extends Command
{
    private final SaveService saveService;

    public DeleteSaveCommand(SaveService saveService)
    {
        this.saveService = saveService;
    }

    @Override
    public boolean execute(Game game)
    {
        if (!hasSecondWord()) {
            System.out.println("Delete which save?");
            System.out.println("Usage: delete-save <saveName>");
            return false;
        }

        try {
            System.out.println(saveService.deleteSave(game, getSecondWord()));
        } catch (SaveException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
