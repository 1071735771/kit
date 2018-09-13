package com.lnwazg.kit.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 最适合shell自动化测试的方式，但是不能有效地获取输出流的数据，并没有中文乱码的问题，并且可以有效地通过工具包解析出shell返回的结果<br>
 * 有待实现全自动化测试的功能，包括自动发布包的功能<br>
 * 这是一个很有潜力的好框架！<br>
 * 提供这几个功能： secure remote login, secure file transfer, and secure TCP/IP and X11 forwarding.<br>
 * http://blog.csdn.net/liuhenghui5201/article/details/50970492   解决sftp编码乱码问题的方案
 * @author nan.li
 * @version 2017年5月16日
 */
public class JavaExpectOverJsch
{
    /**
     * 非常棒的解析执行结果内容的工具类
     * @author nan.li
     * @param source
     * @return
     */
    private static String decode(String source)
    {
        if (StringUtils.isNotEmpty(source))
        {
            if (source.split("\n").length >= 3)
            {
                String[] arrays = source.split("\n");
                List<String> contentList = new ArrayList<>();
                for (int i = 1; i < arrays.length - 1; i++)
                {
                    contentList.add(arrays[i]);
                }
                return String.join("\n", contentList);
            }
        }
        return null;
    }
    
}
