package com.minispring.beans.factory;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.config.Scope;

/**
 * 可配置的BeanFactory接口
 * 提供配置BeanFactory的方法
 */
/**
 * /**
 *  * ConfigurableListableBeanFactory
 *  * 是一个功能更强大的接口，提供了对 Bean 工厂的全面控制能力。
 *  * 支持获取 Bean 定义、预初始化单例 Bean、批量操作等功能。
 *  * 以及它继承接口的全部功能
 *  * 主要用于框架内部实现，开发者很少直接使用。
 *  * ConfigurableBeanFactory
 *  * 是一个基础接口，专注于 Bean 工厂的配置能力。
 *  * 支持作用域管理、类型解析、后置处理器注册等功能。
 *  * 开发者可以通过该接口对 Bean 工厂进行一些基本的配置操作。
 *  */

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory{
    //单例作用域
    String SCOPE_SINGLETON = "singleton";
    //多例作用域
    String SCOPE_PROTOTYPE = "prototype";


    /**
     * 注册作用域
     * @param scopeName 作用域名称
     * @param scope 作用域实现
     */
    void registerScope(String scopeName, Scope scope);

    /**
     * 获取注册的作用域
     * @param scopeName 作用域名称
     * @return 作用域实现，如果未找到则返回null
     */
    Scope getRegisteredScope(String scopeName);
    /**
     * 获取Bean的类型
     * @param name Bean的名称
     * @return Bean的类型
     * @throws BeansException 如果无法获取类型
     */
    Class<?> getType(String name) throws BeansException;
    /**
     * 添加BeanPostProcessor
     * @param beanPostProcessor 要添加的BeanPostProcessor
     *
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    /**
     * 销毁单例Bean
     */
    void destroySingletons();
}
