package com.minispring.context;
import com.minispring.beans.BeansException;
import com.minispring.core.env.ConfigurableEnvironment;

/**
 * 可配置的ApplicationContext接口
 * 提供配置应用上下文的方法
 * 遵循单一职责原则 和 接口隔离原则
 */
/**
 * 设计目标
 * 提供一个可配置的接口，支持对应用上下文的动态调整和控制。
 * 面向框架内部或高级用户，不直接暴露给普通开发者。
 * 通过分离接口，Spring 可以在不破坏现有接口的情况下，添加新的功能。
 * 例如，未来可以为 ConfigurableApplicationContext 添加更多的配置方法，而不会影响到 ApplicationContext。
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 刷新应用上下文
     * 初始化或重新加载应用上下文，包括加载配置文件、注册 Bean 定义、初始化单例 Bean 等。
     * 这是 Spring 启动过程中最重要的方法之一。
     * @throws BeansException 如果刷新过程中发生错误
     */
    void refresh() throws BeansException;

    /**
     * 关闭应用上下文
     * 释放资源（如销毁单例 Bean、关闭线程池等）。
     */
    void close();

    /**
     * 发布应用事件
     * 发布应用事件，触发监听器的响应。
     * @param event 要发布的事件
     */
    void publishEvent(ApplicationEvent event);

    /**
     * 获取Environment
     *
     * @return 可配置的Environment
     */
    ConfigurableEnvironment getEnvironment();

    /**
     * 设置Environment
     *
     * @param environment 可配置的Environment
     */
    void setEnvironment(ConfigurableEnvironment environment);
}