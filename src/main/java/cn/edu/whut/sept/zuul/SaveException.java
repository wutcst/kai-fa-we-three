package cn.edu.whut.sept.zuul;

/**
 * 存档相关业务异常，携带面向用户的提示信息。
 */
public class SaveException extends Exception
{
    public SaveException(String message)
    {
        super(message);
    }
}
