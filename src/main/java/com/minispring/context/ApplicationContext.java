package com.minispring.context;


import com.minispring.beans.factory.ListableBeanFactory;
import com.minispring.core.env.Environment;
/**
 * 应用上下文（Application Context） 是 Spring 框架中的一个核心概念，它是 Spring IoC 容器的
 * 具体表现形式之一，负责实例化、配置和组装 Bean。应用上下文不仅提供了 Bean 工厂的所有功能，
 * 还扩展了更多企业级的功能，使得应用程序能够更好地与 Spring 的高级特性集成。
 */
/**
 * ApplicationContext接口
 * Spring应用上下文，继承ListableBeanFactory，提供更多企业级功能
 *  遵循单一职责原则 和 接口隔离原则
 */

/**
 * 设计目标
 * 提供一个只读的、面向用户的接口，专注于应用上下文的核心功能。
 * 不包含修改或配置上下文的方法，确保其稳定性。
 */
public interface ApplicationContext  extends ListableBeanFactory {
    
    /**
     * 获取应用上下文的名称
     * @return 应用上下文名称
     */
    String getApplicationName();
    
    /**
     * 获取应用上下文的启动时间
     * @return 启动时间（毫秒）
     */
    long getStartupDate();
    
    /**
     * 获取Environment
     * @return Environment
     */
    Environment getEnvironment();
} 