package com.lnwazg.kit.shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.SystemUtils;

/**
 * 命令行工具
 * @author Administrator
 * @version 2016年2月13日
 */
public class CmdUtils
{
    /**
     * 默认的退出值
     */
    private static final int DEFAULT_EXIT_VALUE = 0;
    
    /**
     * 执行某条命令<br>
     * 采用默认的exitValue：0
     * @author Administrator
     * @param cmd
     */
    public static void execute(String cmd)
    {
        execute(cmd, DEFAULT_EXIT_VALUE);
    }
    
    /**
     * 执行某条命令<br>
     * 指定exitValue
     * @author Administrator
     * @param cmd
     * @param exitValue
     */
    public static void execute(String cmd, int exitValue)
    {
        execute(cmd, exitValue, null, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定一批exitValue
     * @author nan.li
     * @param cmd
     * @param exitValues
     */
    public static void execute(String cmd, int[] exitValues)
    {
        execute(cmd, exitValues, null, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定exitValue<br>
     * 附加成功时候的回调函数 
     * @author nan.li
     * @param cmd
     * @param exitValue
     * @param successCallback
     */
    public static void execute(String cmd, int exitValue, SuccessCallback successCallback)
    {
        execute(cmd, exitValue, successCallback, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定一批exitValue<br>
     * 附加成功时候的回调函数 
     * @author nan.li
     * @param cmd
     * @param exitValues
     * @param successCallback
     */
    public static void execute(String cmd, int[] exitValues, SuccessCallback successCallback)
    {
        execute(cmd, exitValues, successCallback, null);
    }
    
    public static void execute(String cmd, int exitValue, FailCallback failCallback)
    {
        execute(cmd, exitValue, null, failCallback);
    }
    
    public static void execute(String cmd, int[] exitValues, FailCallback failCallback)
    {
        execute(cmd, exitValues, null, failCallback);
    }
    
    /**
     * 执行某个方法,成功时候回调某个函数，失败的时候回调另一个函数
     * @author Administrator
     * @param line
     * @param exitValue
     * @param successCallback
     * @param failCallback
     */
    public static void execute(String cmd, int exitValue, SuccessCallback successCallback, FailCallback failCallback)
    {
        execute(cmd, new int[] {exitValue}, successCallback, failCallback);
    }
    
    public static void execute(String cmd, int[] exitValues, SuccessCallback successCallback, FailCallback failCallback)
    {
        CommandLine cmdLine = CommandLine.parse(cmd);
        try
        {
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);
            executor.setExitValues(exitValues);
            int ret = executor.execute(cmdLine);
            
            //控制台编码
            String outputEncoding = "";
            if (SystemUtils.IS_OS_WINDOWS)
            {
                outputEncoding = "GBK";
            }
            else if (SystemUtils.IS_OS_UNIX)
            {
                outputEncoding = CharEncoding.UTF_8;
            }
            else
            {
                outputEncoding = CharEncoding.ISO_8859_1;
            }
            String out = outputStream.toString(outputEncoding);
            String error = errorStream.toString(outputEncoding);
            //执行过程中的控制台输出
            System.out.println(out + error);
            //执行安装程序
            System.out.println(String.format("Cmd exitValue:%s", ret));
            if (successCallback != null)
            {
                successCallback.execute();
            }
        }
        catch (ExecuteException e1)
        {
            e1.printStackTrace();
            if (failCallback != null)
            {
                failCallback.execute(e1);
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            if (failCallback != null)
            {
                failCallback.execute(e1);
            }
        }
    }
    
    /**
     * 成功时候的回调函数
     * @author Administrator
     * @version 2016年2月13日
     */
    public static interface SuccessCallback
    {
        void execute();
    }
    
    /**
     * 失败时刻的回调函数
     * @author Administrator
     * @version 2016年2月13日
     */
    public static interface FailCallback
    {
        void execute(final Exception e);
    }
}
