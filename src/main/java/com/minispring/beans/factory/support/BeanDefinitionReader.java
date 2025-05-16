package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;

/**
 * Bean定义读取接口
 * 定义从不同来源读取Bean定义方法
 */
public interface BeanDefinitionReader {
    /**
     * 获取Bean定义注册器
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取资源加载器
     */
    ResourceLoader getResourceLoader();

    /**
     * 从资源中加载Bean定义
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 从多个资源中加载Bean定义
     */
    void loadBeanDefinitions(Resource... resources) throws BeansException;
    /**
     * 从指定位置中加载Bean定义
     */
    void loadBeanDefinitions(String location) throws BeansException;
    /**
     * 从多个位置中加载Bean定义
     */
    void loadBeanDefinitions(String... locations) throws BeansException;
}
