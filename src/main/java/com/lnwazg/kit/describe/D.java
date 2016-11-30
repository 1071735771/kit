package com.lnwazg.kit.describe;

import java.util.List;
import java.util.Map;

public class D
{
    /**
     * 同样的描述对象的方法
     * @author nan.li
     * @param o
     */
    public static void d(Object o, String... describes)
    {
        if (o instanceof List<?>)
        {
            DescribeUtils.describeList((List<?>)o);
        }
        else if (o instanceof Map<?, ?>)
        {
            DescribeUtils.describeMap((Map<?, ?>)o);
        }
        else if (o instanceof byte[])
        {
            DescribeUtils.describeByteArray((byte[])o, describes);
        }
        else
        {
            DescribeUtils.describe(o);
        }
    }
    
    /**
     * 描述当前的系统环境目录的状态
     * @author nan.li
     */
    public static void dSystem()
    {
        D.d(String.format("user.dir：%s", System.getProperty("user.dir")));
        D.d(String.format("java.io.tmpdir：%s", System.getProperty("java.io.tmpdir")));
        D.d(String.format("java.library.path：%s", System.getProperty("java.library.path")));
    }
}
