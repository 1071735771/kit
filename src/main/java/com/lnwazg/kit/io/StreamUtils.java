package com.lnwazg.kit.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * I/O stream的帮助类
 * 
 * @author lKF20528
 * @version C02 2010-7-8
 * @since OpenEye TAPS V100R001C02
 */
public final class StreamUtils
{
    /**
    * Logger for this class
    */
    private static final Log logger = LogFactory.getLog(StreamUtils.class);
    
    /**
     * 构造函数
     */
    private StreamUtils()
    {
        //阻止调用构造方法
    }
    
    /** 
     * 同时关闭多个流对象
     * @param streams   待关闭的流对象，可以使用数组或多参数的方式传入多个参数值
     * @see [类、类#方法、类#成员]
     */
    public static void close(Closeable... streams)
    {
        if ((null == streams) || (streams.length == 0))
        {
            return;
        }
        for (Closeable stream : streams)
        {
            IOUtils.closeQuietly(stream);
        }
    }
    
    public static void close(Socket... sockets)
    {
        for (Socket socket : sockets)
        {
            if (socket != null && !socket.isClosed())
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }
    
    /**
     * 关闭HTTP连接
     * 
     * @param urlConnection
     *            HTTP连接实例
     */
    public static void disconnect(HttpURLConnection urlConnection)
    {
        if (null == urlConnection)
        {
            return;
        }
        urlConnection.disconnect();
    }
}
