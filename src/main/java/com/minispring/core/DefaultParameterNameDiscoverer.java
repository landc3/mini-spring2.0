package com.minispring.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 默认参数名称发现器
 * 基于Java 8的反射API实现参数名称发现
 */
public class DefaultParameterNameDiscoverer implements ParameterNameDiscoverer {


    /**
     * 根据方法获取参数名称
     *
     * @param method
     * @return
     */
    @Override
    public String[] getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();//获取方法的参数
        String[] parameterNames = new String[parameters.length];//参数名称数组

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isNamePresent()) {//判断参数是否具有名称
                parameterNames[i] = parameter.getName();//获取参数名称
            } else {
                parameterNames[i] = "arg" + i;//默认参数名称
            }
        }
        return parameterNames;
    }

    /**
     * 根据构造方法获取参数名称
     *
     * @param constructor
     * @return
     */
    @Override
    public String[] getParameterNames(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isNamePresent()) {
                parameterNames[i] = parameter.getName();
            } else {
                parameterNames[i] = "arg" + i;
            }
        }
        return parameterNames;
    }
}