package com.lnwazg.kit.describe;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mchange.lang.ByteUtils;

/**
 * 描述一个对象的工具类
 * @author nan.li
 * @version 2016年4月21日
 */
public class DescribeUtils
{
    /**
     * 描述一个map的内容
     * @author nan.li
     * @param map
     */
    public static void describeMap(Map<?, ?> map)
    {
        for (Object key : map.keySet())
        {
            System.out.println(String.format("key:%s   value:%s", key, map.get(key)));
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static void describeList(List<?> listMap)
    {
        int num = 1;
        for (Object obj : listMap)
        {
            System.out.println(String.format("第%s条数据：", num++));
            if (obj instanceof Map)
            {
                for (Object key : ((Map)obj).keySet())
                {
                    System.out.println(String.format("key:%s   value:%s", key, ((Map)obj).get(key)));
                }
            }
            else
            {
                System.out.println(String.format("%s", obj));
            }
        }
    }
    
    public static void describeArray(Object[] array)
    {
        if (array != null && array.length > 0)
        {
            for (int i = 0; i < array.length; i++)
            {
                System.out.println(String.format("第%s条数据：%s", i, array[i]));
            }
            
        }
        else
        {
            System.out.println("param array is null or empty!");
        }
    }
    
    public static void describeByteArray(byte[] o, String[] describes)
    {
        System.out.println(String.format("%s: %s", (describes != null ? StringUtils.join(describes, " ") : ""), ByteUtils.toHexAscii(o)));
    }
    
    public static void describe(Object object)
    {
        System.out.println(object);
    }
    
}
