package cn.edu.whut.sept.zuul;

/**
 * 对应数据库 player 表中的玩家档案记录。
 */
public class PlayerRecord
{
    private final long id;
    private final String name;
    private final double maxWeight;
    private final String createdAt;
    private final String updatedAt;

    public PlayerRecord(long id, String name, double maxWeight, String createdAt, String updatedAt)
    {
        this.id = id;
        this.name = name;
        this.maxWeight = maxWeight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public double getMaxWeight()
    {
        return maxWeight;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public String getUpdatedAt()
    {
        return updatedAt;
    }
}
