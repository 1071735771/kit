package com.lnwazg.kit.singleton;

/**
 * BeanMgr的简易使用版<br>
 * 以后，真正用的最多的，应该是g(),s()两个方法，毕竟其简便易操作啊！<br>
 * 这个类的存在意义，和jQuery的$完全一致！
 * @author Administrator
 * @version 2016年4月17日
 */
public class B
{
    /**
     * 存储一个类的实例
     * @author Administrator
     * @param t
     */
    public static <T> void put(T t)
    {
        BeanMgr.put(t);
    }
    
    /**
     * 取出一个类的实例<br>
     * 假如查不到这个类的实例，则会返回一个默认的实例
     * @author Administrator
     * @param clazz
     * @return
     */
    public static <T> T get(Class<T> clazz)
    {
        return BeanMgr.get(clazz);
    }
    
    public static <T> void set(T t)
    {
        put(t);
    }
    
    public static <T> T g(Class<T> clazz)
    {
        return get(clazz);
    }
    
    public static <T> void s(T t)
    {
        put(t);
    }
    
    /**
     * 将某个类的代理实例设置到注册表中
     * @author nan.li
     * @param clazz
     * @param t
     */
    public static <T> void s(Class<T> clazz, T t)
    {
        BeanMgr.put(clazz,t);
    }
    
    /**
     * 为某个类的一个新对象注入一些实例参数<br>
     * 注入的标记是加了@Resource注解的
     * @author nan.li
     * @param clazz
     * @param objects
     */
    public static <T> void i(Class<T> clazz, Object... objects)
    {
        BeanMgr.inject(clazz, objects);
    }
    
    /**
     * 为某个对象注入一些实例参数<br>
     * 注入的标记是加了@Resource注解的
     * @author nan.li
     * @param t
     * @param objects
     */
    public static <T> void i(T t, Object... objects)
    {
        BeanMgr.inject(t, objects);
    }
    
}
