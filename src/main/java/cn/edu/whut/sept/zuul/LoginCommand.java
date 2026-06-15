package cn.edu.whut.sept.zuul;

import java.sql.SQLException;

public class LoginCommand extends Command
{
    private final PlayerRepository playerRepository;

    public LoginCommand(PlayerRepository playerRepository)
    {
        this.playerRepository = playerRepository;
    }

    @Override
    public boolean execute(Game game)
    {
        if (!hasSecondWord()) {
            System.out.println("Login who?");
            System.out.println("Usage: login <username>");
            return false;
        }

        String username = getSecondWord();
        try {
            PlayerLoginResult result = playerRepository.login(username);
            game.applyPlayerLogin(result);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("登录失败，请稍后重试。");
            System.err.println("Login error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("登录失败：" + e.getMessage());
        }

        return false;
    }
}
