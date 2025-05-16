package com.minispring.context.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.ConfigurableListableBeanFactory;
import com.minispring.beans.factory.config.BeanFactoryPostProcessor;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.context.ApplicationEvent;
import com.minispring.context.ConfigurableApplicationContext;
import com.minispring.context.event.*;
import com.minispring.core.env.ConfigurableEnvironment;
import com.minispring.core.env.StandardEnvironment;
import com.minispring.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * ApplicationContext的抽象实现，实现了ApplicationContext接口
 * 提供模板方法模式的刷新流程
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    //应用上下文名称
    private String applicationName ="";
    //应用上下文启动时间
    private long startupDate;
    //事件多播器
    private ApplicationEventMulticaster applicationEventMulticaster;

    /**
     * 环境对象
     */
    private ConfigurableEnvironment environment;

    /**
     * 默认构造函数
     */
    public AbstractApplicationContext() {
    }

    /**
     * 刷新应用上下文，这是一个模板方法，定义了刷新应用上下文的标准流程
     * 初始化或重新加载应用上下文，包括加载配置文件、注册 Bean 定义、初始化单例 Bean 等。
     * 这是 Spring 启动过程中最重要的方法之一。
     * @throws BeansException 如果刷新过程中发生错误
     */
    @Override
    public void refresh() throws BeansException {
        //1.  准备刷新上下文环境
        prepareRefresh();
        //2. 创建BeanFactory，并加载BeanDefinition
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        //3. 准备BeanFactory，设置类加载器等
        prepareBeanFactory(beanFactory);
        try {
            //4. 允许子类在标准初始化后修改应用上下文的内部BeanFactory
            postProcessBeanFactory(beanFactory);
            //5. 调用BeanFactoryPostProcessor，修改BeanDefinition
            invokeBeanFactoryPostProcessors(beanFactory);
            //6. 注册BeanPostProcessor，这些处理器在Bean初始化时使用
            registerBeanPostProcessors(beanFactory);
            //7. 初始化事件多播器
            initApplicationEventMulticaster();
            //8.初始化特定子类其他Bean
            onRefresh();
            //9. 注册监听器
            registerListeners();
            //10. 完成所有单例Bean的实例化
            finishBeanFactoryInitialization(beanFactory);
            //11. 完成刷新过程，发布事件
            finishRefresh();
        }catch (BeansException ex) {
            // 12. 销毁已创建的单例Bean
            destroyBeans();
            // 13. 重置上下文活动标志
            cancelRefresh(ex);

            // 重新抛出异常
            throw ex;
        }
    }
    /**
     * 准备刷新上下文环境
     */
    protected void prepareRefresh() {
        // 记录启动时间
        this.startupDate = System.currentTimeMillis();

        // 初始化环境变量
        initPropertySources();

        // 获取环境并校验必要的属性
        getEnvironment();
    }
    /**
     * 初始化属性源
     * 子类可以重写此方法以自定义初始化逻辑
     */
    protected void initPropertySources() {
        // 默认实现为空，子类可以覆盖
    }
    /**
     * 获取新的BeanFactory
     *
     * @return 可配置的ListableBeanFactory
     */
    protected abstract ConfigurableListableBeanFactory obtainFreshBeanFactory();

    /**
     * 准备BeanFactory，设置类加载器等
     *
     * @param beanFactory 可配置的ListableBeanFactory
     */
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 添加ApplicationContextAwareProcessor，处理Aware接口
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }
    /**
     * 允许子类在标准初始化后修改应用上下文的内部BeanFactory
     *
     * @param beanFactory BeanFactory
     * @throws BeansException 如果处理过程中发生错误
     */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 默认实现为空，子类可以覆盖
    }

    /**
     * 调用BeanFactoryPostProcessor，修改BeanDefinition
     * @param beanFactory BeanFactory
     * throws BeansException 如果处理过程中发生错误
     */
    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 获取所有BeanFactoryPostProcessor
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessors = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);

        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors.values()){
            postProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 注册BeanPostProcessor，这些处理器在Bean初始化时使用
     *
     * @param beanFactory BeanFactory
     * @throws BeansException 如果处理过程中发生错误
     */
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 获取所有BeanPostProcessor
        Map<String, BeanPostProcessor> beanPostProcessors = beanFactory.getBeansOfType(BeanPostProcessor.class);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors.values()){
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    /**
     * 初始化事件多播器
     *
     * @throws BeansException 如果初始化过程中发生错误
     */
    protected void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();// 获取BeanFactory
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
    }


    /**
     * 获取Bean工厂
     *
     * @return ConfigurableListableBeanFactory
     * @throws IllegalStateException 如果工厂尚未创建
     */
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;


    /**
     * 初始化特定子类的其他bean
     *
     * @throws BeansException 如果初始化过程中发生错误
     */
    protected void onRefresh() throws BeansException{
        // 默认实现为空，子类可以覆盖
    }
    /**
     * 注册监听器
     *
     * @throws BeansException 如果注册过程中发生错误
     */
    protected void registerListeners() throws BeansException {
        // 获取所有ApplicationListener类型的Bean
        Collection<ApplicationListener> listeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : listeners){ // 注册监听器
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }

    /**
     * 完成所有单例Bean的实例化
     *
     * @param beanFactory BeanFactory
     * @throws BeansException 如果初始化过程中发生错误
     */
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        // 初始化所有剩余的单例Bean
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 完成刷新过程，发布事件
     *
     * @throws BeansException 如果发布过程中发生错误
     */
    protected void finishRefresh() {
        // 记录启动时间
       this.startupDate = System.currentTimeMillis();
       // 发布上下文刷新事件
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 销毁已创建的单例Bean
     */
    protected void destroyBeans() {
        getBeanFactory().destroySingletons();
    }

    /**
     * 重置上下文活动标志
     * @param ex 错误
     *
     */
    protected void cancelRefresh(Throwable ex) {
        // 默认实现为空，子类可以覆盖
    }

    /**
     * 关闭上下文
     */
    @Override
    public void close() {
      //  发布上下文关闭事件
        publishEvent(new ContextClosedEvent(this));
        // 销毁所有单例Bean
        destroyBeans();
    }

    /**
     * 发布应用事件
     *
     * @param event 事件
     */
    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);

    }


    /**
     * 设置应用上下文名称
     *
     * @param applicationName 应用上下文名称
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 获取应用上下文名称
     *
     * @return 应用上下文名称
     */
    @Override
    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * 获取启动时间
     *
     * @return 启动时间
     */
    @Override
    public long getStartupDate() {
        return this.startupDate;
    }

    /**
     * 从Bean工厂获取Bean
     *
     * @param name Bean名称
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    /**
     * 从Bean工厂获取指定类型的Bean
     *
     * @param name Bean名称
     * @param requiredType Bean类型
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    /**
     * 从Bean工厂获取指定类型的Bean
     *
     * @param requiredType Bean类型
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(requiredType);
    }

    /**
     * 从Bean工厂获取带构造参数的Bean
     *
     * @param name Bean名称
     * @param args 构造参数
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    /**
     * 检查Bean是否存在
     *
     * @param name Bean名称
     * @return 如果存在返回true
     */
    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }


    /**
     * 从Bean工厂获取指定类型的所有Bean
     *
     * @param type Bean类型
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取Bean工厂中所有Bean的名称
     *
     * @return Bean名称数组
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }


    /**
     * 获取当前环境对象
     * 如果不存在，则创建标准环境
     *
     * @return 环境对象
     */
    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    /**
     * 创建环境对象
     * 子类可以重写此方法以提供自定义环境
     *
     * @return 新创建的环境对象
     */
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }
    /**
     * 设置环境对象
     */
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }




}
