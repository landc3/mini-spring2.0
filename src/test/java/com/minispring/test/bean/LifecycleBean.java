package com.minispring.test.bean;

/**
 * 生命周期Bean类
 * 用于测试初始化和销毁方法
 */

/**
 * （1）特点
 * 自定义初始化和销毁方法：
 * 初始化方法：init()
 * 销毁方法：destroy()
 * 灵活性高：
 * 用户可以自由命名初始化和销毁方法。
 * 不依赖于特定的接口或框架。
 * 需要显式配置：
 * 在使用时，需要在配置文件或注解中明确指定初始化和销毁方法。
 * （2）使用场景
 * 当你希望在 Bean 的生命周期中执行自定义逻辑，并且不希望依赖 Spring 的特定接口时，可以使用这种方式。
 * 示例配置（XML 配置方式）：
 * xml
 * 深色版本
 * <bean id="lifecycleBean" class="com.example.LifecycleBean" init-method="init" destroy-method="destroy">
 *     <property name="name" value="TestBean"/>
 * </bean>
 * 示例配置（注解方式）：
 * java
 * 深色版本
 * @Bean(initMethod = "init", destroyMethod = "destroy")
 * public LifecycleBean lifecycleBean() {
 *     return new LifecycleBean();
 * }
 * （3）优点
 * 灵活性高，适合与非 Spring 框架集成。
 * 方法名可以自由定义，便于理解。
 * （4）缺点
 * 需要在配置文件或注解中显式指定初始化和销毁方法，增加了配置复杂度。
 */
public class LifecycleBean {
    
    private String name;
    private boolean initialized = false;
    private boolean destroyed = false;
    
    public LifecycleBean() {
        System.out.println("LifecycleBean构造函数");
    }
    
    public void init() {
        System.out.println("LifecycleBean初始化方法");
        this.initialized = true;
    }
    
    public void destroy() {
        System.out.println("LifecycleBean销毁方法");
        this.destroyed = true;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    @Override
    public String toString() {
        return "LifecycleBean{" +
                "name='" + name + '\'' +
                ", initialized=" + initialized +
                ", destroyed=" + destroyed +
                '}';
    }
} 