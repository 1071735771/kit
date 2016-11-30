package com.lnwazg.kit.url;

import org.apache.commons.lang3.StringUtils;

public class UriUtils
{
    /**
     * 移除uri中的参数
     * @author nan.li
     * @param uri
     * @return
     */
    public static String removeParams(String uri)
    {
        if (StringUtils.isNotEmpty(uri))
        {
            if (uri.indexOf("?") != -1)
            {
                uri = uri.substring(0, uri.indexOf("?"));
            }
            uri = uri.trim();
        }
        return uri;
    }
    
}
