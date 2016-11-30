package com.lnwazg.kit.type;

/**
 * 字符串类型的一些简单的实际判断
 * @author nan.li
 * @version 2016年7月29日
 */
public class StrTypeUtils
{
    public static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    public static boolean isLong(String str)
    {
        try
        {
            Long.parseLong(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    public static boolean isDouble(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
}
