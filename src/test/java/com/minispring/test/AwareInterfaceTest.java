package com.minispring.test;

import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.test.bean.AwareBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 测试Aware接口
 */
public class AwareInterfaceTest {
    @Test
    void  testBeanNameAwareAndBeanFactoryAware(){
            //1.创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //2.注册bean定义
        BeanDefinition beanDefinition = new BeanDefinition(AwareBean.class);
        beanFactory.registerBeanDefinition("awareBean", beanDefinition);
        //3.获取bean
        AwareBean awareBean = beanFactory.getBean("awareBean", AwareBean.class);

        //验证BeanNameAware和BeanFactoryAware接口是否被正确实现
        assertEquals("awareBean",awareBean.getBeanName());
        assertSame(beanFactory,awareBean.getBeanFactory());

        System.out.println("BeanNameAware接口测试通过：" + awareBean.getBeanName());
        System.out.println("BeanFactoryAware接口测试通过：" + awareBean.getBeanFactory());
    }
}
