package com.minispring.beans.factory.config;

/**
 * Bean引用
 * 表示对其他Bean的引用，在属性注入和构造函数中注入使用
 * 为解决循环依赖提供支持
 *  如果属性值是一个引用（如 userDao），则使用 BeanReference 表示
 */
public class BeanReference {
    private final String beanName;

    /**
     * 创建一个Bean引用
     *
     * @param beanName 被引用的Bean名称
     */
    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 获取被引用的Bean名称
     *
     * @return Bean名称
     */
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public String toString() {
        return "对Bean[" + this.beanName + "]的引用";
    }

}
