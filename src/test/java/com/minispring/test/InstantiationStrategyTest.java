package com.minispring.test;

import com.minispring.beans.BeanWrapper;
import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.support.CglibSubclassingInstantiationStrategy;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.beans.factory.support.InstantiationStrategy;
import com.minispring.beans.factory.support.SimpleInstantiationStrategy;
import com.minispring.test.bean.TestBean;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

/**
 *实例化策略和Bean包装器测试
 * InstantiationStrategy,BeanWrapper测试
 */
public class InstantiationStrategyTest {
    /**
     * 测试JDK反射实例化策略
     */
    /**
     * 测试JDK反射实例化策略
     */
    @Test
    public void testSimpleInstantiationStrategy() throws Exception {
        //创建BeanFactory对象
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //创建BeanDefinition对象
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);
        //创建jdk反射实例化策略
        InstantiationStrategy strategy = new SimpleInstantiationStrategy();

        //使用无参构造函数实例化
        Object bean1 = strategy.instantiate(beanDefinition, "testBean", null, null);
        assertNotNull(bean1);// 验证实例是否不为空
        assertTrue(bean1 instanceof TestBean);// 验证实例是否为TestBean类型
        //使用有参构造函数实例化
        Constructor<?> constructor = TestBean.class.getDeclaredConstructor(String.class, int.class);
        Object bean2 = strategy.instantiate(beanDefinition, "testBean", constructor, new Object[]{"test", 1});
        assertNotNull(bean2);
        assertTrue(bean2 instanceof TestBean);
        assertEquals("test", ((TestBean) bean2).getName());// 验证实例的name属性是否为"test"
        assertEquals(1, ((TestBean) bean2).getAge());// 验证实例的age属性是否为1
    }

    /**
     * 测试CGLIB实例化策略
     */
    @Test
    public void testCglibSubclassingInstantiationStrategy() throws Exception {
        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 创建CGLIB实例化策略
        InstantiationStrategy strategy = new CglibSubclassingInstantiationStrategy();

        // 使用无参构造函数实例化
        Object bean1 = strategy.instantiate(beanDefinition, "testBean", null, null);
        assertNotNull(bean1);
        assertTrue(bean1 instanceof TestBean);

        // 使用有参构造函数实例化
        Constructor<?> ctor = TestBean.class.getDeclaredConstructor(String.class, int.class);
        Object bean2 = strategy.instantiate(beanDefinition, "testBean", ctor, new Object[]{"test", 42});
        assertNotNull(bean2);// 验证实例是否不为空
        assertTrue(bean2 instanceof TestBean);// 验证实例是否为TestBean类型
        assertEquals("test", ((TestBean) bean2).getName());
        assertEquals(42, ((TestBean) bean2).getAge());
    }
    /**
     * 测试类型转换和属性填充
     */
    @Test
    public void testTypeConversionAndPropertyFilling() {
        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 创建BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        //设置属性值
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "John"));
        propertyValues.addPropertyValue(new PropertyValue("age", "42"));
        beanDefinition.setPropertyValues(propertyValues);

        // 注册BeanDefinition
        beanFactory.registerBeanDefinition("testBean", beanDefinition);

        //获取Bean
        TestBean bean = (TestBean) beanFactory.getBean("testBean");
        //验证属性值是否正确
        assertEquals("John", bean.getName());
        assertEquals(42, bean.getAge());
    }

    //测试BeanWapper
    @Test
    public void testBeanWrapper() {
        //创建测试Bean对象
        TestBean testBean = new TestBean("test",42);
        //创建BeanWrapper
        BeanWrapper beanWrapper = new BeanWrapper(testBean);

        //测试获取Bean包装的实例
        Object wrappedInstance = beanWrapper.getWrappedInstance();
        assertNotNull(wrappedInstance);
        assertTrue(wrappedInstance instanceof TestBean);
        assertEquals(testBean, wrappedInstance);// 验证包装实例是否与原始实例相同

        //测试获取Bean包装的类型
        Class<?> wrappedClass = beanWrapper.getWrappedClass();
        assertEquals(TestBean.class, wrappedClass);

    }
    /**
     * 测试BeanWrapper的嵌套属性支持
     */
    @Test
    public void testNestedProperty() {}

    /**
     * 测试Bean类
     */
    public static class TestBean {
        // 使用public字段，便于测试
        public String name;
        public int age;
        private TestBean child;

        public TestBean() {
        }

        public TestBean(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public TestBean getChild() {
            return child;
        }

        public void setChild(TestBean child) {
            this.child = child;
        }
    }
}
