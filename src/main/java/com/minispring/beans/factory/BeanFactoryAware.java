package com.minispring.beans.factory;

import com.minispring.beans.BeansException;

/**
 * BeanFactoryAware接口，用于设置BeanFactory实例
 * 实现该接口的bean会在创建过程中注入BeanFactory，从而可以访问BeanFactory中的资源。
 */
public interface BeanFactoryAware {
    /**
     * 设置BeanFactory实例
     * @param beanFactory BeanFactory实例
     * 在bean属性填充后，初始化调用前
     */
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
