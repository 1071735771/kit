package com.lnwazg.kit.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 轻巧的Http请求工具类，并且依赖最小化
 * @author nan.li
 * @version 2016年9月20日
 */
public final class HttpUtils
{
    /**
    * Logger for this class
    */
    private static final Log logger = LogFactory.getLog(HttpUtils.class);
    
    /**
     * HTTP请求方法: POST
     */
    public static final String POST_METHOD = "POST";
    
    /**
     * HTTP请求方法: GET
     */
    public static final String GET_METHOD = "GET";
    
    /**
     * 日志实例
     */
    private static final String NEW_LINE = "\r\n";
    
    private static final String CONTAIN_SYMBOL = "&";
    
    private static final String EQUAL_SYMBOL = "=";
    
    // 超时时间
    private static final String CONNECT_TIMEOUT = "20000";
    
    /**
     *  返回的html所采用的默认编码方式
     */
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    public static void main(String[] args)
    {
        try
        {
            //            System.out.println(send("http://www.qq.com", "GET", null, null, null));
            System.out.println(get("http://www.qq.com", "GBK"));
            //            System.out.println(get("http://www.baidu.com"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static String get(String url)
        throws IOException
    {
        return get(url, DEFAULT_ENCODING);
    }
    
    public static String get(String url, String encoding)
        throws IOException
    {
        return send(url, GET_METHOD, null, null, null, encoding);
    }
    
    public static String post(String url, Map<String, String> params, Map<String, String> headerInfo)
        throws IOException
    {
        return post(url, params, headerInfo, DEFAULT_ENCODING);
    }
    
    public static String post(String url, Map<String, String> params, Map<String, String> headerInfo, String encoding)
        throws IOException
    {
        return send(url, POST_METHOD, null, null, null, encoding);
    }
    
    /**
     * 发送HTTP请求
     */
    public static String send(String urlString, String method, Map<String, String> parameters, Map<String, String> properties, String type)
        throws IOException
    {
        return send(urlString, method, parameters, properties, type, DEFAULT_ENCODING);
    }
    
    /**
     * 发送HTTP请求
     * 
     * @param urlString
     *            HTTP请求URL
     * @param method
     *            HTTP请求方法
     * @param parameters
     *            HTTP请求参数
     * @param properties
     *            HTTP请求附件挑起，作为互相功能的扩展
     * @param type
     *            当请求为METHOD为POST时:
     *            type=1,则代表参数parameters体现在url中，即http://XXXXXXXXX
     *            /?param1=12&param2=23 
     *            type=2,则代表参数parameters体现在请求消息体中。
     * @return 响应对象
     * @throws IOException
     *             IO异常
     */
    public static String send(String urlString, String method, Map<String, String> parameters, Map<String, String> properties, String type, String encoding)
        throws IOException
    {
        // GET请求
        if (GET_METHOD.equalsIgnoreCase(method))
        {
            return sendGetRequest(urlString, parameters, properties, encoding);
        }
        // POST请求
        if (POST_METHOD.equalsIgnoreCase(method))
        {
            return sendPostRequest(urlString, parameters, properties, type, encoding);
        }
        return null;
    }
    
    /**
     * 
     * 发送HTTP Get请求
     * 
     * @param urlString
     *            请求URL
     * @param parameters
     *            请求参数
     * @param properties
     *            HTTP请求附件挑起，作为互相功能的扩展
     * @return
     * @throws IOException
     */
    private static String sendGetRequest(String urlString, Map<String, String> parameters, Map<String, String> properties, String encoding)
        throws IOException
    {
        // 如果有参数，拼装完整的URL
        if (null != parameters && !parameters.isEmpty())
        {
            StringBuffer param = new StringBuffer();
            if (urlString.indexOf("?") != -1)
            {
                //url中已经追加过参数了，那么再追加就应该用&符号
                param.append("&");
            }
            else
            {
                param.append('?');
            }
            for (Map.Entry<String, String> entry : parameters.entrySet())
            {
                param.append(entry.getKey()).append(EQUAL_SYMBOL).append(entry.getValue()).append(CONTAIN_SYMBOL);
            }
            // 拼装完整的URL，并去掉最后一个&符号
            urlString += param.substring(0, param.length() - 1);
        }
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            // 设置超时时间20秒
            urlConnection.setConnectTimeout(Integer.valueOf(CONNECT_TIMEOUT));
            urlConnection.setRequestMethod(GET_METHOD);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            //若properties非空,则在连接中追加上properties属性
            if (properties != null)
            {
                for (Map.Entry<String, String> entry : properties.entrySet())
                {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Fail to send http request. URL:" + urlString, e);
            throw e;
        }
        return getContent(urlString, urlConnection, encoding);
    }
    
    /**
     * 
     * 发送HTTP Post请求
     * 
     * @param urlString
     *            请求URL
     * @param parameters
     *            请求参数
     * @param properties
     *            HTTP请求附件挑起，作为互相功能的扩展
     * @param type
     *            当请求为METHOD为POST时:
     *            type=1,则代表参数parameters体现在url中，即http://XXXXXXXXX/?param1=12&param2=23
     *            type=2,则代表参数parameters体现在请求消息体中。
     * @return
     * @throws IOException
     */
    private static String sendPostRequest(String urlString, Map<String, String> parameters, Map<String, String> properties, String type, String encoding)
        throws IOException
    {
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        
        try
        {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            
            // 设置超时时间20秒
            urlConnection.setConnectTimeout(Integer.valueOf(CONNECT_TIMEOUT));
            urlConnection.setRequestMethod(POST_METHOD);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            if (properties != null)
            {
                for (Map.Entry<String, String> entry : properties.entrySet())
                {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (parameters != null && !parameters.isEmpty())
            {
                StringBuffer param = new StringBuffer();
                if ("1".equals(type))
                {
                    for (Map.Entry<String, String> entry : parameters.entrySet())
                    {
                        param.append(entry.getKey()).append(EQUAL_SYMBOL).append(entry.getValue());
                        param.append(CONTAIN_SYMBOL);
                    }
                }
                else if ("2".equals(type))
                {
                    for (Map.Entry<String, String> entry : parameters.entrySet())
                    {
                        param.append(entry.getValue());
                    }
                }
                else
                {
                    //默认情况下，同第一种情况
                    for (Map.Entry<String, String> entry : parameters.entrySet())
                    {
                        param.append(entry.getKey()).append(EQUAL_SYMBOL).append(entry.getValue());
                        param.append(CONTAIN_SYMBOL);
                    }
                }
                out = urlConnection.getOutputStream();
                out.write(param.toString().getBytes());
                urlConnection.getOutputStream().flush();
            }
        }
        catch (IOException e)
        {
            logger.error("Fail to send http request. URL:" + urlString, e);
            throw e;
        }
        finally
        {
            close(out);
        }
        return getContent(urlString, urlConnection, encoding);
    }
    
    /**
     * 发送HTTP请求
     * 
     * @param urlString
     *            HTTP请求URL
     * @param method
     *            HTTP请求方法
     * @param properties
     *            HTTP请求附件挑起，作为互相功能的扩展
     * @return 响应对象
     * @throws IOException
     *             IO异常
     */
    public static Map<String, String> sendByHeader(String urlString, String method, Map<String, String> properties)
        throws IOException
    {
        // 发送请求
        if (POST_METHOD.equalsIgnoreCase(method) || GET_METHOD.equalsIgnoreCase(method))
        {
            return sendRequestByHeader(urlString, method, properties);
        }
        return null;
    }
    
    /**
     * 
     * 发送HTTP请求 参数放在消息头中
     * 
     * @param urlString
     *            请求URL
     * @param method
     *            请求方法
     * @param properties
     *            HTTP请求附件挑起，作为互相功能的扩展
     * @return Map<String,String>
     * @throws IOException
     */
    private static Map<String, String> sendRequestByHeader(String urlString, String method, Map<String, String> properties)
        throws IOException
    {
        HttpURLConnection urlConnection = null;
        
        try
        {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            
            // 设置超时时间20秒
            urlConnection.setConnectTimeout(Integer.valueOf(CONNECT_TIMEOUT));
            if (POST_METHOD.equalsIgnoreCase(method))
            {
                urlConnection.setRequestMethod(POST_METHOD);
            }
            else if (GET_METHOD.equalsIgnoreCase(method))
            {
                urlConnection.setRequestMethod(GET_METHOD);
            }
            
            urlConnection.setUseCaches(false);
            
            if (properties != null)
            {
                for (Map.Entry<String, String> entry : properties.entrySet())
                {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
        }
        catch (IOException e)
        {
            logger.error("Fail to send http request. URL:" + urlString, e);
            throw e;
        }
        
        return getResponseHeader(urlString, urlConnection);
    }
    
    /**
     * 得到响应消息头字段
     * 
     * @param urlConnection
     * @return 响应对象
     * @throws IOException
     */
    private static Map<String, String> getResponseHeader(String urlString, HttpURLConnection urlConnection)
        throws IOException
    {
        Map<String, String> respHeader = new HashMap<String, String>();
        
        // 将返回码及响应头字段放入map中
        try
        {
            String returnCode = String.valueOf(urlConnection.getResponseCode());
            Map<String, List<String>> properties = urlConnection.getHeaderFields();
            respHeader.put("returnCode", returnCode);
            
            if (properties != null)
            {
                for (Map.Entry<String, List<String>> entry : properties.entrySet())
                {
                    StringBuffer temp = new StringBuffer();
                    List<String> list = entry.getValue();
                    Iterator<String> iter = list.iterator();
                    
                    while (iter.hasNext())
                    {
                        temp.append(iter.next() + ",");
                    }
                    
                    String value = temp.substring(0, temp.length() - 1);
                    respHeader.put(entry.getKey(), value);
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Fail to get value from response of Http. URL:" + urlString, e);
            throw e;
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
        
        return respHeader;
    }
    
    /**
     * 得到响应对象
     * 
     * @param urlConnection
     * @return 响应对象
     * @throws IOException
     */
    private static String getContent(String urlString, HttpURLConnection urlConnection, String encoding)
        throws IOException
    {
        InputStream in = null;
        BufferedReader bufferedReader = null;
        String content = null;
        try
        {
            in = urlConnection.getInputStream();
            // 获取编码格式，如果HTTP返回编码格式不存在，则采用默认编码方式
            bufferedReader = new BufferedReader(new InputStreamReader(in, encoding));
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null)
            {
                temp.append(line).append(NEW_LINE);
                line = bufferedReader.readLine();
            }
            content = temp.toString();
        }
        catch (IOException e)
        {
            logger.error("Fail to get value from response of Http. URL:" + urlString, e);
            throw e;
        }
        finally
        {
            disconnect(urlConnection);
            close(bufferedReader, in);
        }
        return content;
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
