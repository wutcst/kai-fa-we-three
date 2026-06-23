package cn.edu.whut.sept.zuul;

import java.sql.SQLException;

/**
 * register 命令 —— 注册新账号（已有账号则报错）。
 */
public class RegisterCommand extends Command {
    private final PlayerRepository playerRepository;
    private String thirdWord;

    public RegisterCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void setThirdWord(String word) { this.thirdWord = word; }

    @Override
    public boolean execute(Game game) {
        if (!hasSecondWord()) {
            System.out.println("Register who?");
            System.out.println("Usage: register <username> <password>");
            return false;
        }

        String username = getSecondWord();
        String password = (thirdWord != null && !thirdWord.isEmpty()) ? thirdWord : "";

        if (password.isEmpty()) {
            System.out.println("注册需要设置密码！");
            return false;
        }

        try {
            PlayerRecord existing = playerRepository.findByName(username);
            if (existing != null) {
                System.out.println("该用户名已存在，请直接登录。");
                return false;
            }
            PlayerRecord created = playerRepository.createPlayer(username, password);
            game.applyPlayerLogin(new PlayerLoginResult(created, true));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            game.logout();
        } catch (SQLException e) {
            System.out.println("注册失败，请稍后重试。");
            game.logout();
        }

        return false;
    }
}
