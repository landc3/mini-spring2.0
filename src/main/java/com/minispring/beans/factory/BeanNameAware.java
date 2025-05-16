package com.minispring.beans.factory;

/**
 * BeanNameAware接口，用于获取Bean的名字
 * 实现此接口的bean在创建过程中会被注入当前Bean的名称
 */
public interface BeanNameAware {
    /**
     * 设置当前Bean的名称
     * 在bean属性填充后，初始化调用前
     * @param beanName 当前Bean的名称
     */
    void setBeanName(String beanName);
}
