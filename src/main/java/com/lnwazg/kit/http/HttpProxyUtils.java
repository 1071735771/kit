package com.lnwazg.kit.http;

/**
 * 系统代理工具
 * 
 * @author  lnwazg
 * @version  [版本号, 2012-10-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class HttpProxyUtils
{
    
    private static final String PROXY_HOST = "127.0.0.1";
    
    private static final String PROXY_PORT = "8087";
    
    /** 
     * 设置系统代理
     * @see [类、类#方法、类#成员]
     */
    public static void setProxy()
    {
        System.getProperties().setProperty("proxySet", "true");
        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以  
        System.getProperties().setProperty("http.proxyHost", PROXY_HOST);
        System.getProperties().setProperty("http.proxyPort", PROXY_PORT);
    }
    
    /** 
     * 取消系统代理
     * @see [类、类#方法、类#成员]
     */
    public static void cancelProxy()
    {
        System.getProperties().setProperty("proxySet", "false");
    }
    
}
