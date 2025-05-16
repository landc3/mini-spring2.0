package com.minispring.test.processor;


import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanPostProcessor;

/**
 * 自定义BeanPostProcessor
 * 用于测试BeanPostProcessor的功能
 */
public class CustomBeanPostProcessor implements BeanPostProcessor {
    /**
     * 在Bean实例化之后，初始化之前调用
     * @param bean 原始的Bean实例
     * @param beanName Bean的名称
     * @return 处理后的Bean实例
     * @return null 表示返回值仍是原始的Bean实例
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor前置处理: " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor后置处理: " + beanName);
        return bean;
    }
}
