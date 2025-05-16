package com.minispring.test;

import com.minispring.beans.BeansException;
import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.context.ApplicationContext;
import com.minispring.test.bean.TestUserDao;
import com.minispring.test.bean.TestUserService;
import com.minispring.test.bean.TestUserServiceWithConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 依赖注入测试类
 *
 */
public class DependencyInjectionTest {


    /**
     * 测试属性注入
     * 验证 Spring 的属性注入机制是否能够正确地将属性值和依赖注入到目标 Bean 中。
     * 具体来说，测试以下内容：
     * 普通属性注入：将字符串 "张三" 注入到 TestUserService 的 name 属性中。
     * 依赖注入：将 TestUserDao 的实例注入到 TestUserService 的 userDao 属性中。
     */
    /**
     * 关键概念解析
     * 3.1 BeanDefinition
     * 描述一个 Bean 的元信息，包括：
     * Bean 的类名。
     * Bean 的作用域（单例或原型）。
     * 构造函数参数、属性值等。
     * 在这个例子中，TestUserDao 和 TestUserService 的 BeanDefinition 分别描述了它们的类和属性。
     * 3.2 PropertyValues
     * 存储一个 Bean 的所有属性值。
     * 每个属性值由 PropertyValue 表示，包含属性名称和对应的值。
     * 如果属性值是一个引用（如 userDao），则使用 BeanReference 表示。
     * 3.3 BeanReference
     * 表示对另一个 Bean 的引用。
     * 在这个例子中，userDao 属性被设置为对 "userDao" Bean 的引用。
     */
    @Test
    public void testPropertyInjection() {
        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册UserDao
        BeanDefinition userDaoBeanDefinition = new BeanDefinition(TestUserDao.class);
        beanFactory.registerBeanDefinition("userDao", userDaoBeanDefinition);

        // 创建UserService的属性值
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "张三"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        // 注册UserService
        BeanDefinition userServiceBeanDefinition = new BeanDefinition(TestUserService.class, propertyValues);
        beanFactory.registerBeanDefinition("userService", userServiceBeanDefinition);

        // 获取UserService
        TestUserService userService = (TestUserService) beanFactory.getBean("userService");

        // 验证属性注入
        assertEquals("张三", userService.getName());
        assertNotNull(userService.getUserDao());
        assertEquals("UserDao", userService.getUserDao().toString());
    }
    /**
     * 测试构造函数注入
     *
     */
    @Test
    public void testConstructorInjection() {
        //创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //注册UserDao
        BeanDefinition userDaoBeanDefinition = new BeanDefinition(TestUserDao.class);
        beanFactory.registerBeanDefinition("userDao", userDaoBeanDefinition);
        //注册userService
        BeanDefinition userServiceBeanDefinition = new BeanDefinition(TestUserServiceWithConstructor.class);
        beanFactory.registerBeanDefinition("userServiceBeanDefinition", userServiceBeanDefinition);
        //获取userService，通过构造函数注入userDao
        System.out.println("开始获取userServiceWithConstructor（自动装配）");
        TestUserServiceWithConstructor userService = (TestUserServiceWithConstructor) beanFactory.getBean("userServiceBeanDefinition",new Object[]{beanFactory.getBean("userDao")});
        System.out.println("获取到userServiceWithConstructor: " + userService);
        System.out.println("userDao: " + userService.getUserDao());
        /**
         * 在 Java 中，当你将一个对象直接打印到控制台时（例如通过 System.out.println）
         * ，会调用该对象的 toString() 方法来生成字符串表示形式。
         * 所以"userDao: " + userService.getUserDao()=="userDao: UserDao"
         */
        //验证构造函数注入
        assertNotNull(userService.getUserDao());//验证userDao是否不为空
        assertEquals("UserDao", userService.getUserDao().toString());
    }


}
