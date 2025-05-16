package com.minispring.beans.factory;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.config.Scope;

/**
 * 可配置的ListableBeanFactory接口
 * 提供配置BeanFactory的方法
 */
/**
 * ConfigurableListableBeanFactory
 * 是一个功能更强大的接口，提供了对 Bean 工厂的全面控制能力。
 * 支持获取 Bean 定义、预初始化单例 Bean、批量操作等功能。
 * 以及它继承接口的全部功能
 * 主要用于框架内部实现，开发者很少直接使用。
 * ConfigurableBeanFactory
 * 是一个基础接口，专注于 Bean 工厂的配置能力。
 * 支持作用域管理、类型解析、后置处理器注册等功能。
 * 开发者可以通过该接口对 Bean 工厂进行一些基本的配置操作。
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory,AutowireCapableBeanFactory,ConfigurableBeanFactory{

    /**
     * 根据名称获取所有BeanDefinition
     *
     * @param beanName Bean名称
     * return BeanDefinition
     * throws BeansException
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
    /**
     * 预初始化所有单例Bean
     * @throws BeansException
     */
    void preInstantiateSingletons() throws BeansException;

    /**
     * 获取所有Bean定义的名称
     * @return Bean定义名称数组
     */
    String[] getBeanDefinitionNames();
    /**
     * 添加BeanPostProcessor
     *
     * @param beanPostProcessor 要添加的处理器
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

}
