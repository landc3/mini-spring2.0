package com.minispring.test;

import com.minispring.context.ApplicationContext;
import com.minispring.context.support.ClassPathXmlApplicationContext;
import com.minispring.test.bean.TestBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApplicationContext测试类
 * 测试ApplicationContext的基本功能
 */
public class ApplicationContextTest {
    /**
     * 测试从路径Xml加载Bean
     */
    @Test
    public void testClassPathXmlApplicationContext() {
        //  创建ApplicationContext
       ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
       // 从ApplicationContext中获取Bean
        TestBean testBean = context.getBean("testBean", TestBean.class);
        // 验证Bean
        assertNotNull(testBean, "Bean不应该为null");
        assertEquals("测试Bean", testBean.getName(), "属性值应该正确");

        //测试单例
        Object testBean2 = context.getBean("testBean", TestBean.class);
        assertEquals(testBean, testBean2, "两个Bean应该是同一个实例");

        //测试BeanFactory方法
        assertTrue(context.containsBean("testBean"), "ApplicationContext应该包含testBean");
        assertFalse(context.containsBean("nonExistBean"), "ApplicationContext不应该包含nonExistBean");

        //测试Bean列表
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        assertNotNull(beanDefinitionNames, "Bean名称数组不应为null");
        assertTrue(beanDefinitionNames.length > 0, "Bean名称数组应包含元素");

        //输出所有Bean的名称
        System.out.println("所有Bean名称:");
        for (String name : beanDefinitionNames) {
            System.out.println("- " + name);
        }


    }
}
