package com.lnwazg.kit.http;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 * 网络连接管理器
 * @author nan.li
 * @version 2014-11-6
 */
@SuppressWarnings("deprecation")
public class HttpConnectionManager
{
    /**
     * 连接超时时间
     */
    //    private static final int CON_TIMEOUT = 30000;
    private static final int CON_TIMEOUT = 5000;
    
    /**
     * socket等待超时时间
     */
    //    private static final int SO_TIMEOUT = 30000;
    private static final int SO_TIMEOUT = 5000;
    
    /**
     * win7 chrome的userAgent
     */
    private static String UA_WINDOW7_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";
    
    /**
     * 每次返回一个新的HttpClient实例
     * @author nan.li
     * @return
     */
    public static DefaultHttpClient getHttpClient()
    {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(200);
        
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.connection.timeout", Integer.valueOf(CON_TIMEOUT));
        params.setParameter("http.socket.timeout", Integer.valueOf(SO_TIMEOUT));
        params.setParameter("http.useragent", UA_WINDOW7_CHROME);
        
        DefaultHttpClient client = new DefaultHttpClient(cm, params);
        return client;
    }
}