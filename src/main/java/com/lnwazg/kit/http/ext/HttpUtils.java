package com.lnwazg.kit.http.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 进阶版的工具类<br>
 * 支持https的使用
 * @author nan.li
 * @version 2016年9月20日
 */
public class HttpUtils
{
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    private static final String METHOD_POST = "POST";
    
    private static final String METHOD_GET = "GET";
    
    private static SSLContext ctx = null;
    
    private static HostnameVerifier verifier = null;
    
    private static SSLSocketFactory socketFactory = null;
    
    public static final String XML_CONTENT_TYPE = "text/xml";
    
    public static final int CONNECT_TIME_OUT = 10000;
    
    public static final int READ_TIME_OUT = 10000;
    
    private static class DefaultTrustManager implements X509TrustManager
    {
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
        
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException
        {
        }
        
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException
        {
        }
    }
    
    static
    {
        try
        {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
            ctx.getClientSessionContext().setSessionTimeout(15);
            ctx.getClientSessionContext().setSessionCacheSize(1000);
            socketFactory = ctx.getSocketFactory();
        }
        catch (Exception e)
        {
            //ignore
        }
        verifier = new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                return false;//默认认证不通过，进行证书校验。
            }
        };
    }
    
    private HttpUtils()
    {
    }
    
    /**
     * 执行HTTP POST请求。
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
        throws IOException
    {
        return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }
    
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout, int readTimeout)
        throws IOException
    {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        byte[] content = {};
        if (query != null)
        {
            content = query.getBytes(charset);
        }
        return doPost(url, ctype, content, connectTimeout, readTimeout);
    }
    
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param ctype   请求类型
     * @param content 请求字节数组
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
        throws IOException
    {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try
        {
            try
            {
                conn = getConnection(new URL(url), METHOD_POST, ctype);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            }
            catch (IOException e)
            {
                Map<String, String> map = getParamsFromUrl(url);
                logger.error("doPost failed,URL ={}", url, e);
                throw e;
            }
            try
            {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsString(conn);
            }
            catch (IOException e)
            {
                Map<String, String> map = getParamsFromUrl(url);
                logger.error("doPost failed,URL ={},content={}", url, content, e);
                throw e;
            }
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
            if (conn != null)
            {
                conn.disconnect();
            }
        }
        return rsp;
    }
    
    /**
     * 执行Get请求
     * @author nan.li
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url)
        throws IOException
    {
        return doGet(url, null);
    }
    
    /**
     * 执行HTTP GET请求。
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params)
        throws IOException
    {
        return doGet(url, params, DEFAULT_CHARSET);
    }
    
    /**
     * 执行HTTP GET请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params, String charset)
        throws IOException
    {
        HttpURLConnection conn = null;
        String rsp = null;
        try
        {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(params, charset);
            try
            {
                conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
            }
            catch (IOException e)
            {
                Map<String, String> map = getParamsFromUrl(url);
                logger.error("doGet failed,URL ={},params={}", url, params, e);
                throw e;
            }
            try
            {
                rsp = getResponseAsString(conn);
            }
            catch (IOException e)
            {
                Map<String, String> map = getParamsFromUrl(url);
                logger.error("doGet failed,URL ={},params={}", url, params, e);
                throw e;
            }
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
        return rsp;
    }
    
    private static HttpURLConnection getConnection(URL url, String method, String ctype)
        throws IOException
    {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol()))
        {
            HttpsURLConnection connHttps = (HttpsURLConnection)url.openConnection();
            connHttps.setSSLSocketFactory(socketFactory);
            connHttps.setHostnameVerifier(verifier);
            conn = connHttps;
        }
        else
        {
            conn = (HttpURLConnection)url.openConnection();
        }
        
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }
    
    private static URL buildGetUrl(String strUrl, String query)
        throws IOException
    {
        URL url = new URL(strUrl);
        if (StringUtils.isBlank(query))
        {
            return url;
        }
        if (StringUtils.isBlank(url.getQuery()))
        {
            if (strUrl.endsWith("?"))
            {
                strUrl = strUrl + query;
            }
            else
            {
                strUrl = strUrl + "?" + query;
            }
        }
        else
        {
            if (strUrl.endsWith("&"))
            {
                strUrl = strUrl + query;
            }
            else
            {
                strUrl = strUrl + "&" + query;
            }
        }
        return new URL(strUrl);
    }
    
    public static String buildQuery(Map<String, String> params, String charset)
        throws IOException
    {
        if (params == null || params.isEmpty())
        {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;
        for (Map.Entry<String, String> entry : entries)
        {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
            {
                if (hasParam)
                {
                    query.append("&");
                }
                else
                {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }
        return query.toString();
    }
    
    protected static String getResponseAsString(HttpURLConnection conn)
        throws IOException
    {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null)
        {
            return getStreamAsString(conn.getInputStream(), charset);
        }
        else
        {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isBlank(msg))
            {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            }
            else
            {
                throw new IOException(msg);
            }
        }
    }
    
    private static String getStreamAsString(InputStream stream, String charset)
        throws IOException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();
            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0)
            {
                writer.write(chars, 0, count);
            }
            return writer.toString();
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
    
    private static String getResponseCharset(String ctype)
    {
        String charset = DEFAULT_CHARSET;
        
        if (!StringUtils.isEmpty(ctype))
        {
            String[] params = ctype.split(";");
            for (String param : params)
            {
                param = param.trim();
                if (param.startsWith("charset"))
                {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2)
                    {
                        if (!StringUtils.isEmpty(pair[1]))
                        {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        
        return charset;
    }
    
    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value)
    {
        return decode(value, DEFAULT_CHARSET);
    }
    
    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public static String encode(String value)
    {
        return encode(value, DEFAULT_CHARSET);
    }
    
    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, String charset)
    {
        String result = null;
        if (StringUtils.isNotBlank(value))
        {
            try
            {
                result = URLDecoder.decode(value, charset);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    
    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, String charset)
    {
        String result = null;
        if (StringUtils.isNotBlank(value))
        {
            try
            {
                result = URLEncoder.encode(value, charset);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    
    private static Map<String, String> getParamsFromUrl(String url)
    {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1)
        {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null)
        {
            map = new HashMap<String, String>();
        }
        return map;
    }
    
    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query)
    {
        Map<String, String> result = new HashMap<String, String>();
        
        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0)
        {
            for (String pair : pairs)
            {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2)
                {
                    result.put(param[0], param[1]);
                }
            }
        }
        return result;
    }
}
