package com.lnwazg.kit.converter;

import org.apache.commons.lang.ObjectUtils;

/**
 * 对象值转换器<br>
 * 将某个对象以指定的类型返回，例如getAsString()、getAsBoolean()等等
 * @author nan.li
 * @version 2017年7月22日
 */
public class ValueConverter
{
    /**
     * 数值对象
     */
    private Object value;
    
    /**
     * 构造函数 
     * @param value
     */
    public ValueConverter(Object value)
    {
        this.value = value;
    }
    
    /**
     * 默认的获取值的方式
     * @author nan.li
     * @return
     */
    public String get()
    {
        return ObjectUtils.toString(value);
    }
    
    public String getAsString()
    {
        return get();
    }
    
    public boolean getAsBoolean()
    {
        return Boolean.valueOf(get());
    }
    
    public double getAsDouble()
    {
        return Double.valueOf(get());
    }
    
    public float getAsFloat()
    {
        return Float.valueOf(get());
    }
    
    public long getAsLong()
    {
        return Long.valueOf(get());
    }
    
    public int getAsInt()
    {
        return Integer.valueOf(get());
    }
    
    public byte getAsByte()
    {
        return Byte.valueOf(get());
    }
    
    public short getAsShort()
    {
        return Short.valueOf(get());
    }
}
