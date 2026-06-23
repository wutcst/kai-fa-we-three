package cn.edu.whut.sept.zuul;

/**
 * logout 命令 —— 退出当前登录账号。
 */
public class LogoutCommand extends Command {
    @Override
    public boolean execute(Game game) {
        game.logout();
        System.out.println("已退出登录。");
        return false;
    }
}
