package cn.edu.whut.sept.zuul;

/**
 * 表示尝试创建已存在用户名时抛出的异常。
 */
public class DuplicatePlayerException extends RuntimeException
{
    public DuplicatePlayerException(String name)
    {
        super("Player already exists: " + name);
    }

    public DuplicatePlayerException(String name, Throwable cause)
    {
        super("Player already exists: " + name, cause);
    }
}
