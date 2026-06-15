package cn.edu.whut.sept.zuul;

/**
 * 玩家登录结果，区分新注册与已有玩家登录。
 */
public class PlayerLoginResult
{
    private final PlayerRecord player;
    private final boolean newlyCreated;

    public PlayerLoginResult(PlayerRecord player, boolean newlyCreated)
    {
        this.player = player;
        this.newlyCreated = newlyCreated;
    }

    public PlayerRecord getPlayer()
    {
        return player;
    }

    public boolean isNewlyCreated()
    {
        return newlyCreated;
    }
}
