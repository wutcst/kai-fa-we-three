package cn.edu.whut.sept.zuul;

public class SaveCommand extends Command
{
    private final SaveService saveService;

    public SaveCommand(SaveService saveService)
    {
        this.saveService = saveService;
    }

    @Override
    public boolean execute(Game game)
    {
        if (!hasSecondWord()) {
            System.out.println("Save to which slot?");
            System.out.println("Usage: save <saveName>");
            return false;
        }

        try {
            System.out.println(saveService.save(game, getSecondWord()));
        } catch (SaveException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
