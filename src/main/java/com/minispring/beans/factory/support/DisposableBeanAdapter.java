package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.DisposableBean;

import java.lang.reflect.Method;

/**
 * DisposableBean适配器
 * 用于统一处理实现了DisposableBean接口的Bean和配置了destroy-method的Bean
 * 这是一个适配器类，用于统一处理 DisposableBean 和自定义销毁方法。
 * 它封装了两种销毁逻辑：
 * 如果 Bean 实现了 DisposableBean 接口，则调用其 destroy() 方法。
 * 如果配置了自定义销毁方法，则通过反射调用该方法。
 */
public class DisposableBeanAdapter implements DisposableBean {
    /**
     * bean	Object	实际的 Bean 对象，表示 Spring 容器中管理的具体实例。
     * beanName	String	Bean 的名称，在 Spring 容器中作为唯一标识符，用于区分不同的 Bean。
     */
    private final Object bean;// 目标Bean
    private final String beanName;// Bean名称
    private final String destroyMethodName;// 销毁方法名称
    
    /**
     * 构造函数
     * 
     * @param bean 目标Bean
     * @param beanName Bean名称
     * @param destroyMethodName 销毁方法名称
     */
    public DisposableBeanAdapter(Object bean, String beanName, String destroyMethodName) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = destroyMethodName;
    }
    
    /**
     * 执行Bean的销毁方法
     * 1. 如果Bean实现了DisposableBean接口，则调用其destroy方法
     * 2. 如果Bean配置了destroy-method，则通过反射调用该方法
     */
    @Override
    public void destroy() throws Exception {
        // 1. 如果Bean实现了DisposableBean接口，则调用其destroy方法
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
            System.out.println("执行Bean[" + beanName + "]的DisposableBean接口的destroy方法");
        }
        
        // 2. 如果Bean配置了destroy-method且不是DisposableBean接口中的方法，则通过反射调用该方法
        /**
         * destroyMethodName != null含义：
         * 检查 destroyMethodName 是否为非空。
         * 如果 destroyMethodName 是 null，说明没有配置自定义销毁方法，因此不需要执行反射调用。
         * 背景：
         * 在 Spring 中，可以通过 XML 配置或注解指定一个 Bean 的销毁方法（如 destroy-method="customDestroy"）。
         * 如果没有显式配置销毁方法，destroyMethodName 将为 null。
         *
         * !(bean instanceof DisposableBean && "destroy".equals(destroyMethodName))含义：
         * 分解解释：
         * bean instanceof DisposableBean：
         * 检查当前 Bean 是否实现了 DisposableBean 接口。
         * 如果实现了 DisposableBean 接口，则该 Bean 已经有一个标准的 destroy() 方法，Spring 容器会直接调用它（在前面的代码中已经处理了）。
         * "destroy".equals(destroyMethodName)：
         * 检查 destroyMethodName 是否等于 "destroy"。
         * 如果 destroyMethodName 被配置为 "destroy"，这可能意味着用户希望通过自定义方式调用 DisposableBean 接口的 destroy 方法。
         * bean instanceof DisposableBean && "destroy".equals(destroyMethodName)：
         * 这个条件的意思是：如果 Bean 实现了 DisposableBean 接口，并且用户显式配置了 destroy-method="destroy"，那么这两个逻辑实际上是重复的。
         * Spring 不希望重复调用同一个 destroy 方法，因此在这种情况下跳过反射调用。
         * ! 取反：
         * 最终，这个条件表示：只有当 Bean 没有实现 DisposableBean 接口，或者 destroyMethodName 不是 "destroy" 时，才会进入反射调用的逻辑。
         */
        if (destroyMethodName != null && !(bean instanceof DisposableBean && "destroy".equals(destroyMethodName))) {
            try {
                // 获取Bean的销毁方法
                Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
                // 调用Bean的销毁方法
                destroyMethod.invoke(bean);
                System.out.println("执行Bean[" + beanName + "]的自定义销毁方法：" + destroyMethodName);
            } catch (NoSuchMethodException e) {
                throw new BeansException("找不到Bean[" + beanName + "]的销毁方法：" + destroyMethodName, e);
            } catch (Exception e) {
                throw new BeansException("执行Bean[" + beanName + "]的销毁方法[" + destroyMethodName + "]失败", e);
            }
        }
    }
} 