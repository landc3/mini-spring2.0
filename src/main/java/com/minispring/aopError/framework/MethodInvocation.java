package com.minispring.aopError.framework;

import java.lang.reflect.Method;

/**
 * 方法调用的抽象接口
 * 表示一次运行时方法调用
 */
public interface MethodInvocation {
    /**
     * 获取被调用的方法
     */
    Method getMethod();
    /**
     * 获取被调方法的参数
     */
    Object[] getArguments();
    /**
     * 获取被调用的目标对象
     */
    Object getThis();
    /**
     * 获取被调用的拦截器链
     */
    Object proceed() throws Throwable;


}
