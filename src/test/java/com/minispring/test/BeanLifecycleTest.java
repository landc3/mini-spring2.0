package com.minispring.test;

import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.test.bean.LifecycleBeanWithInterface;
import com.minispring.test.bean.LifecycleBean;
import com.minispring.test.bean.LifecycleBeanWithInterface;
import com.minispring.test.processor.CustomBeanPostProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * bean生命周期测试类
 */
public class BeanLifecycleTest {

    @Test
    void testBeanLifecycle() throws Exception {
        //创建Bean工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //添加BeanPostProcessor
        beanFactory.addBeanPostProcessor(new CustomBeanPostProcessor());
        //注册Bean定义，使用配置方法
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("destroy");
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);
        //注册Bean定义，使用接口方法
        BeanDefinition beanDefinition1 = new BeanDefinition(LifecycleBeanWithInterface.class);
        beanFactory.registerBeanDefinition("lifecycleBeanWithInterface", beanDefinition1);
        //获取Bean
        /**
         * 实例化
         * 发生在 getBean() 方法内部。
         * 根据 BeanDefinition 使用反射创建对象。
         * 示例代码中的 LifecycleBean 和 LifecycleBeanWithInterface 在调用 getBean() 时被实例化。
         * 初始化
         * 紧接在实例化之后，仍然在 getBean() 方法内部完成。
         * 包括以下步骤：
         * 属性填充。
         * 执行 BeanPostProcessor 前置处理。
         * 调用初始化方法（如 init-method 或 afterPropertiesSet()）。
         * 执行 BeanPostProcessor 后置处理。
         */
        LifecycleBean lifecycleBean = beanFactory.getBean("lifecycleBean", LifecycleBean.class);
        LifecycleBeanWithInterface lifecycleBeanWithInterface = beanFactory.getBean("lifecycleBeanWithInterface", LifecycleBeanWithInterface.class);

        //验证Bean已经被初始化
        assertTrue(lifecycleBean.isInitialized());// 验证 LifecycleBean 是否已经初始化
        assertFalse(lifecycleBean.isDestroyed());
        assertTrue(lifecycleBeanWithInterface.isInitialized());// 验证 LifecycleBeanWithInterface 是否已经初始化
        assertFalse(lifecycleBeanWithInterface.isDestroyed());

        //销毁Bean
        beanFactory.destroySingletons();
        //验证Bean已经被销毁
        assertTrue(lifecycleBean.isDestroyed());
        assertTrue(lifecycleBeanWithInterface.isDestroyed());


    }
}
