package com.minispring.aopError;

import java.lang.reflect.Method;

/**
 * 方法前置通知接口
 * 在目标方法执行前执行自定义的逻辑
 */
public interface MethodBeforeAdvice extends BeforeAdvice{
    /**
     * 在目标方法执行前执行自定义的逻辑
     * @param method 目标方法
     * @param args 目标方法的参数
     * @param target 目标对象
     * throws Throwable 如果前置通知执行过程中发生异常，则抛出该异常
     */
    void before(Method method, Object[] args, Object target) throws Throwable;
}
