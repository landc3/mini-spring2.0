package com.minispring.beans.factory;

import com.minispring.beans.BeansException;

/**
 * 自动装配的BeanFactory接口
 * 提供自动装配Bean的方法
 */

/**
 * AbstractAutowireCapableBeanFactory
 * 是一个抽象类，提供了具体的 Bean 创建和管理逻辑。
 * 是 Spring IoC 容器的核心实现之一，负责完成 Bean 的实例化、属性填充、初始化和销毁等完整流程。
 * 它提供了具体的 Bean 创建逻辑，支持自动装配（autowiring）和依赖注入（dependency injection）。
 * 支持循环依赖：使用三级缓存机制解决循环依赖问题。
 * 实例化策略：提供了 InstantiationStrategy 接口，允许使用不同的实例化策略（如 CGLIB 或 JDK 动态代理）。
 * 内部实现了 AutowireCapableBeanFactory 接口的功能。
 *
 * AutowireCapableBeanFactory
 * 是一个接口，定义了自动装配和 Bean 生命周期管理的标准。
 * 提供给开发者手动控制 Bean 的装配和生命周期的能力。
 * 它的实现通常由 AbstractAutowireCapableBeanFactory 或其子类完成。
 * 联系与协作
 * AutowireCapableBeanFactory 提供了高层次的功能规范，而 AbstractAutowireCapableBeanFactory 提供了具体的实现。
 * 它们共同构成了 Spring IoC 容器的核心功能，支持灵活的 Bean 管理和装配。
 */
public interface AutowireCapableBeanFactory extends BeanFactory{
    /**
     * 自动装配：按类型
     */
    int AUTOWIRE_BY_TYPE = 1;

    /**
     * 自动装配：按名称
     */
    int AUTOWIRE_BY_NAME = 2;

    /**
     * 自动装配：构造函数
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * 不自动装配
     */
    int AUTOWIRE_NO = 0;

    /**
     * 创建一个新的Bean实例
     *
     * @param beanClass Bean类
     * @return Bean实例
     * @throws BeansException 如果创建过程中发生错误
     */
    Object createBean(Class<?> beanClass) throws BeansException;

    /**
     * 自动装配一个已存在的Bean实例
     *
     * @param existingBean 已存在的Bean实例
     * @param beanName Bean名称
     * @throws BeansException 如果装配过程中发生错误
     */
    void autowireBean(Object existingBean, String beanName) throws BeansException;
    /**
     * 应用BeanPostProcessor前置处理
     * @param existingBean 已存在的Bean实例
     * @param beanName Bean名称
     * @throws BeansException 如果装配过程中发生错误
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;
    /**
     * 应用BeanPostProcessor后置处理
     * @param existingBean 已存在的Bean实例
     * @param beanName Bean名称
     * @throws BeansException 如果装配过程中发生错误
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;

}
