package com.lnwazg.kit.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * Gson转换用的帮助类
 * @author nan.li
 * @version 2016年10月13日
 */
public class GsonHelper
{
    /**
     * gson.toJson(requestStrs)  JsonElement转String、Object转String <br>
     * gson.toJsonTree(src)     Object转JsonElement         <br>
     * gson.fromJson(json, typeOfT)   String转Object<T>,需要使用TypeToken来表示泛型对象的类型 <br>
     * 无须操作，直接转                             JsonElement转Object
     */
    public static Gson gson = new Gson();
    
    /**
     * 格式化输出版本的gson
     */
    public static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * jsonParser.parse(jsonStr)  String转JsonElement
     */
    public static JsonParser jsonParser = new JsonParser();
    
}
