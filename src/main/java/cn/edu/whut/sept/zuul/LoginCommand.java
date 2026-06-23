package cn.edu.whut.sept.zuul;

import java.sql.SQLException;

public class LoginCommand extends Command
{
    private final PlayerRepository playerRepository;

    public LoginCommand(PlayerRepository playerRepository)
    {
        this.playerRepository = playerRepository;
    }

    private String thirdWord;

    public void setThirdWord(String word) { this.thirdWord = word; }

    @Override
    public boolean execute(Game game)
    {
        if (!hasSecondWord()) {
            System.out.println("Login who?");
            System.out.println("Usage: login <username> <password>  (新用户自动注册)");
            return false;
        }

        String username = getSecondWord();
        String password = (thirdWord != null && !thirdWord.isEmpty()) ? thirdWord : "";
        try {
            PlayerLoginResult result = playerRepository.login(username, password);
            game.applyPlayerLogin(result);
        } catch (IllegalArgumentException e) {
            game.logout(); // 清除旧登录状态
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            game.logout();
            System.out.println("登录失败，请稍后重试。");
            System.err.println("Login error: " + e.getMessage());
        } catch (RuntimeException e) {
            game.logout();
            System.out.println("登录失败：" + e.getMessage());
        }

        return false;
    }
}
