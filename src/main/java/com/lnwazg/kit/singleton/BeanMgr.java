package com.lnwazg.kit.singleton;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.lnwazg.kit.cache.JvmMemCacheLite;
import com.lnwazg.kit.cache.key.JvmMemCacheLiteKey;

/**
 * 单例管理器<br>
 * 即开即用，最简化<br>
 * 注入的工具包
 * @author Administrator
 * @version 2016年4月15日
 */
public class BeanMgr
{
    /**
     * 类-实例 映射表<br>
     * 单例模式管理器
     */
    private static Map<Class<?>, Object> SingletonClazz2ObjectMap = new HashMap<>();
    
    /**
     * 存储一个类的实例<br>
     * 根据实例名自动推测出所属的类<br>
     * 注意：此方法可能不适用于动态代理工具生产出来的实例的自动注入！因为动态代理工具生产出来的实例一般是XXXProxy$xxx这样的内部类
     * @author Administrator
     * @param t
     */
    public static <T> void put(T t)
    {
        SingletonClazz2ObjectMap.put(t.getClass(), t);
    }
    
    //弃用，因为此处的泛型定义无意义
    //    /**
    //     * 存储一个类的实例<br>
    //     * 普遍适用的首选方法，尤其适合针对动态代理生成的实例手动指定Class类这样的情况！
    //     * @author nan.li
    //     * @param clazz
    //     * @param t
    //     */
    //    public static <T> void put(Class<T> clazz, T t)
    //    {
    //        SingletonClazz2ObjectMap.put(clazz, t);
    //    }
    
    /**
     * 存储一个类的实例<br>
     * 同样是普遍适用的，但是该方法不限制实例的泛型类型，可以任意指定对象进行注入，包括动态代理工具生产的对象！<br>
     * 这个方法将比put(Class<T> clazz, T t)更强大给力！
     * @author nan.li
     * @param clazz
     * @param object
     */
    public static void put(Class<?> clazz, Object object)
    {
        SingletonClazz2ObjectMap.put(clazz, object);
    }
    
    /**
     * 查找实例
     * 仅查询某个key clazz是否有值，不做额外的实例化操作
     * @author nan.li
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T query(Class<T> clazz)
    {
        return (T)SingletonClazz2ObjectMap.get(clazz);
    }
    
    /**
     * 查找实例（若查不到，则自动实例化一个默认的实例）
     * @author Administrator
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz)
    {
        T t = (T)SingletonClazz2ObjectMap.get(clazz);
        if (t == null)
        {
            //为空，则返回默认的实例
            try
            {
                t = clazz.newInstance();
                put(t);
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return t;
    }
    
    /**
     * 为某种类型注入一堆对象
     * @author nan.li
     * @param clazz
     * @param objects
     */
    public static <T> void injectAndPut(Class<T> clazz, Object... objects)
    {
        try
        {
            T t = clazz.newInstance();
            injectByTypeAndAnno(t, objects);
            put(t);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 为某个对象注入一堆对象
     * @author nan.li
     * @param t
     * @param objects
     */
    public static <T> void injectAndPut(T t, Object... objects)
    {
        injectByTypeAndAnno(t, objects);
        put(t);
    }
    
    /**
     * 根据类型以及注解，进行注入操作
     * @author nan.li
     * @param t
     * @param objects
     */
    @SuppressWarnings("unchecked")
    private static <T> void injectByTypeAndAnno(T t, Object... objects)
    {
        if (objects == null || objects.length == 0)
        {
            return;
        }
        Class<T> clazz = (Class<T>)t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(Resource.class))
            {
                //标明该字段需要被注入
                for (Object object : objects)
                {
                    Class<?> fieldType = field.getType();
                    //如果加了@Resource注解的字段是某个object的接口，那么就将该object注入到该字段里面
                    if (fieldType.isAssignableFrom(object.getClass()))
                    {
                        //两者一致，则执行注入操作！
                        try
                        {
                            field.setAccessible(true);
                            field.set(t, object);
                        }
                        catch (IllegalArgumentException | IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取某个包下面的某个类的实例对象
     * @author Administrator
     * @param packageName
     * @param clazzName
     * @return
     */
    public static Object getBeanByClassName(String packageName, String clazzName)
    {
        String clazzFullName = String.format("%s.%s", packageName, clazzName);
        if (JvmMemCacheLite.get(clazzFullName) != null)
        {
            //此处做了缓存处理，避免了反射造成的性能瓶颈
            return JvmMemCacheLite.get(JvmMemCacheLiteKey.classFullName.name() + clazzFullName);
        }
        Class<?> clazz;
        try
        {
            clazz = Class.forName(clazzFullName);
            Object o = get(clazz);
            JvmMemCacheLite.put(JvmMemCacheLiteKey.classFullName.name() + clazzFullName, o);
            return o;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
