package com.minispring.beans.factory;

/**
 * 层次结构的BeanFactory接口
 * 继承了BeanFactory接口，表示 hierarchical bean factory
 */
public interface HierarchicalBeanFactory extends BeanFactory{

    /**
     * 获取父BeanFactory
     * @return 父BeanFactory，如果没有则返回null
     */
    BeanFactory getParentBeanFactory();

    /**
     * 检查本地Beanfactory中是否包含指定名称的Bean
     *
     */
    boolean containsLocalBean(String name);
}
