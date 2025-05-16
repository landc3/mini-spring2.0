package com.minispring.test.bean;

import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.InitializingBean;

/**
 * 生命周期Bean类（使用接口方式）
 * 实现InitializingBean和DisposableBean接口
 */

/**
 * （1）特点
 * 实现 Spring 提供的接口：
 * 初始化接口：InitializingBean，提供 afterPropertiesSet() 方法。
 * 销毁接口：DisposableBean，提供 destroy() 方法。
 * 强耦合于 Spring 框架：
 * 直接依赖 Spring 的接口，无法脱离 Spring 使用。
 * 无需额外配置：
 * Spring 容器会自动调用 afterPropertiesSet() 和 destroy() 方法，无需显式指定。
 * （2）使用场景
 * 当你完全基于 Spring 框架开发，并且希望利用 Spring 提供的生命周期管理功能时，可以使用这种方式。
 * 示例配置（XML 配置方式）：
 * Xml
 * 深色版本
 * <bean id="lifeCycleBeanWithInterface" class="com.example.LifeCycleBeanWithInterface">
 *     <property name="name" value="TestBean"/>
 * </bean>
 * 示例配置（注解方式）：
 * Java
 * 深色版本
 * @Bean
 * public LifeCycleBeanWithInterface lifeCycleBeanWithInterface() {
 *     return new LifeCycleBeanWithInterface();
 * }
 * （3）优点
 * 无需额外配置，Spring 容器会自动调用生命周期方法。
 * 方法名固定，符合 Spring 的规范，易于维护。
 * （4）缺点
 * 强耦合于 Spring 框架，不适合与其他框架集成。
 * 如果未来不再使用 Spring，这些接口方法将失去意义。
 */
public class LifecycleBeanWithInterface implements InitializingBean, DisposableBean {
    private String name;// 属性
    private boolean initialized = false;// 是否初始化
    private boolean destroyed = false;// 是否销毁

    /**
     * 无参构造器
     */
    public LifecycleBeanWithInterface() {
        System.out.println("LifecycleBeanWithInterface constructor");
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



    /**
     *
     * 实现DisposableBean接口的destroy方法，在销毁前调用
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean接口调用，销毁");
        this.destroyed = true;
    }

    /**
     * 实现InitializingBean接口的afterPropertiesSet方法，在属性设置后调用
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean接口调用，初始化");
        this.initialized = true;
    }
    /**
     * 重写toString方法
     */
    @Override
    public String toString() {
        return "LifecycleBeanWithInterface{" +
                "name='" + name + '\'' +
                ", initialized=" + initialized +
                ", destroyed=" + destroyed +
                '}';
    }
}
