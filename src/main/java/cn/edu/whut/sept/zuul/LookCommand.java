package cn.edu.whut.sept.zuul;

public class LookCommand extends Command {
    @Override
    public boolean execute(Game game) {
        System.out.println("=== 仔细观察当前房间 ===");
        System.out.println(game.getCurrentRoom().getLongDescription());
        System.out.println("当前生命值：" + game.getHp());
        game.setHp(game.getHp() + 1); // 观察+1HP
        System.out.println("观察环境，生命值+1，当前HP：" + game.getHp());
        return false;
    }
}