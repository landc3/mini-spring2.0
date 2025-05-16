package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 简单实例化策略
 * 基于Jdk反射机制实现Bean的实例化策略
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy{

    /**
     * 实例化Bean
     * @param beanDefinition Bean定义
     * @param beanName Bean名称
     * @param constructor 构造函数
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?> constructor, Object[] args) throws BeansException {
        // 获取Bean的Class对象
        Class<?> beanClass = beanDefinition.getBeanClass();
        //2.
        try {
            if (constructor != null) {
                //参数校验
                if (args == null) {
                    // 如果参数为空，则创建一个空的参数数组
                    args = new Object[0];
                }
                //检查参数的个数是否与构造函数的参数个数匹配
                if (args.length != constructor.getParameterCount()) {
                    throw new BeansException("参数个数不匹配");
                }
                //使用指定的构造函数创建实例
                return constructor.newInstance(args);
            } else {
                // 如果没有指定构造函数，则使用默认（无参）构造函数创建实例
                return beanClass.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new BeansException("实例化Bean失败 [" + beanName + "]", e);
        }


    }
}
