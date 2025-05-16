package com.minispring.aopError;
/**
 * 类过滤器接口，用于确定哪些类应该被代理
 */
public interface ClassFilter {
    /**
     * 判断类是否匹配
     * @param clazz
     * @return
     */
    boolean matches(Class<?> clazz);

    /**
     * 默认的类过滤器，匹配所有类
     */
    ClassFilter TRUE =clazz -> true;

}
