package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * Bean实例化策略接口
 * 定义如何实例化Bean的策略，允许使用不同的实例化机制
 */
public interface InstantiationStrategy {
    /**
     * 实例化Bean
     * @param beanDefinition Bean定义
     * @param beanName Bean名称
     * @param constructor 构造函数
     * @param args 构造函数参数
     * @return 实例化后的Bean对象
     * @throws BeansException 如果实例化过程中发生异常
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?> constructor, Object[] args) throws BeansException;

}
