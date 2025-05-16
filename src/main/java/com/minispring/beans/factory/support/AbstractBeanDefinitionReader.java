package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.core.io.DefaultResourceLoader;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;

/**
 * 抽象Bean定义读取器
 * 封装通用的读取逻辑，实现BeanDefinitionReader接口
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader{
    private final BeanDefinitionRegistry registry;//Bean定义注册器
    /**
     * 定义了set方法，所以不能使用final修饰符
     */
    private ResourceLoader resourceLoader;//资源加载器

    /**
     * 构造方法，传入Bean定义注册器和资源加载器
     * @param registry Bean定义注册器
     * @param resourceLoader 资源加载器
     */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }
    /**
     * 构造方法，传入Bean定义注册器，使用默认的资源加载器
     * @param registry Bean定义注册器
     */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
      this(registry, new DefaultResourceLoader());//默认使用DefaultResourceLoader
    }

    /**
     * 设置资源加载器
     * @param resourceLoader 资源加载器
     *（1）为什么需要  setResourceLoader 方法？
     * 支持动态配置：允许用户在运行时指定类加载器。
     * 适应不同环境：满足复杂运行环境下的需求。
     * 提高扩展性：让框架更灵活，适应更多场景。
     * （2）不可变性与可变性的权衡
     * 默认情况下使用不可变的类加载器，保证稳定性。
     * 提供  setResourceLoader 方法，增加灵活性。
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * 获取Bean定义注册器
     * @return Bean定义注册器
     */
    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    /**
     * 获取资源加载器
     * @return 资源加载器
     */
    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }




    /**
     * 加载Bean定义
     * @param resources Bean定义资源数组
     * @throws Exception 如果加载Bean定义时发生异常
     */
    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources){//遍历资源数组
            loadBeanDefinitions(resource);//加载Bean定义
        }
    }

    /**
     * 加载Bean定义
     * @param location Bean定义资源位置
     * @throws Exception 如果加载Bean定义时发生异常
     */
    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        Resource resource = getResourceLoader().getResource(location);
        loadBeanDefinitions(resource);

    }

    /**
     * 加载Bean定义
     * @param locations Bean定义资源位置数组
     * @throws Exception 如果加载Bean定义时发生异常
     */
    @Override
    public void loadBeanDefinitions(String... locations) throws BeansException {
        for (String location : locations){
            loadBeanDefinitions(location);
        }
    }

}
