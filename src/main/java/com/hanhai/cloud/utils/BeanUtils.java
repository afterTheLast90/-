package com.hanhai.cloud.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * @author wmgx
 * @create 2021-02-19-15:33
 **/
public class BeanUtils {

    /**
     * 将传入对象的String类型的属性的空串替换成null
     * @param o 传入的对象
     * @param <T>
     * @return 处理结果
     */
    public  static <T> T replaceEmptyWithNullForString(T o) {

        Field[] fields = ReflectUtil.getFields(o.getClass());

        for (Field field : fields){
            if (field.getType() == String.class){
                field.setAccessible(true);
                try {
                    if ("".equals(field.get(o))){
                        field.set(o,null);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }finally {
                    field.setAccessible(false);
                }
            }
        }
        return o;

    }

    /**
     * 将传入的对象转换为指定的类，并拷贝同名属性
     * @param source 被拷贝的对象
     * @param tClass 转换成的类
     * @param <T>
     * @return 转换结果
     */
    public static <T> T convertTo(Object source,Class<T> tClass) {
        return BeanUtil.toBean(source,tClass);
    }

    /**
     * 将 source中的同名属性赋值到target中
     * @param source 源对象
     * @param target 目标对象
     * @param <T>
     * @return 结果
     */
    public static <T> T copyProperties(Object source, T target){
        BeanUtil.copyProperties(source,target);
        return target;
    }
}
