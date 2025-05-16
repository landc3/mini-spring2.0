package com.minispring.test.environment;

import com.minispring.core.env.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * environmment测试类
 */
public class EnvironmentTest {
    /**
     * 测试StandardEnvironment的基本功能
     */
    @Test
    public void testStandardEnvironment() {
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        //测试系统属性
        System.setProperty("test.property", "test-value");
        assertEquals("test-value", standardEnvironment.getProperty("test.property"));

        //测试不同类型的属性获取
        System.setProperty("test.int", "123");
        System.setProperty("test.boolean", "true");
        assertEquals(123, standardEnvironment.getProperty("test.int", Integer.class).intValue());
        assertTrue(standardEnvironment.getProperty("test.boolean", Boolean.class));

        // 测试默认值
        assertEquals("default-value", standardEnvironment.getProperty("non.existent.property", "default-value"));
        assertEquals(456, standardEnvironment.getProperty("non.existent.int", Integer.class, 456).intValue());

        //清理系统属性
        System.clearProperty("test.property");
        System.clearProperty("test.int");
        System.clearProperty("test.boolean");

    }

    /**
     * 测试Profiles功能
     *
     */
    @Test
    public void testProfiles() {
        AbstractEnvironment abstractEnvironment = new StandardEnvironment();

        //默认情况下只有default profile激活
        assertTrue(abstractEnvironment.acceptsProfiles("default"));
        assertFalse(abstractEnvironment.acceptsProfiles("test"));
        //设置激活的profile
        abstractEnvironment.setActiveProfiles("test", "dev");

        assertTrue(abstractEnvironment.acceptsProfiles("test"));
        assertTrue(abstractEnvironment.acceptsProfiles("dev"));
        assertFalse(abstractEnvironment.acceptsProfiles("prod"));

        //测试多个profiles
        assertTrue(abstractEnvironment.acceptsProfiles("test", "dev")); // 只要有一个匹配就返回true

        //添加profile
        abstractEnvironment.addActiveProfile("prod");
        assertTrue(abstractEnvironment.acceptsProfiles("prod"));

    }
    /**
     * 测试PropertySource的优先级
     */
    @Test
    public void testPropertySourcePriority() {
        MutablePropertySources propertySources = new MutablePropertySources();


        // 添加两个属性源，第一个优先级高于第二个
        MapPropertySource firstSource = new MapPropertySource("first", java.util.Collections.singletonMap("test.key", "first-value"));
        MapPropertySource secondSource = new MapPropertySource("second", java.util.Collections.singletonMap("test.key", "second-value"));

        /**
         * 操作：
         * addFirst 方法将属性源添加到 MutablePropertySources 的开头。
         * 先添加 secondSource，后添加 firstSource，因此 firstSource 的优先级高于 secondSource。
         */
        propertySources.addFirst(secondSource);
        propertySources.addFirst(firstSource);
        /**
         *  创建自定义环境实现测试PropertySource优先级
         *  定义：
         * AbstractEnvironment 是 Spring 中的一个抽象类，用于表示环境（Environment）。
         * 这里通过匿名内部类的方式自定义了一个 Environment 实现。
         * 重写 customizePropertySources 方法：
         * customizePropertySources 是 AbstractEnvironment 提供的一个钩子方法，允许开发者自定义属性源。
         * 在这里：
         * 将 firstSource 添加到属性源列表的开头（优先级最高）。
         * 将 secondSource 添加到属性源列表的末尾（优先级最低）。
         */
        AbstractEnvironment abstractEnvironment = new AbstractEnvironment(){
            @Override
            protected void customizePropertySources(MutablePropertySources propertySources) {
                propertySources.addFirst(firstSource);
                propertySources.addLast(secondSource);
            }

        };
        //应该返回第一个属性源的值
        assertEquals("first-value", abstractEnvironment.getProperty("test.key"));
    }


    /**
     * 测试SystemEnvironmentPropertySource
     */
    @Test
    public void testSystemEnvironmentPropertySource() {
        // 创建一个模拟的环境变量Map
        java.util.Map<String, Object> mockEnv = new java.util.HashMap<>();
        mockEnv.put("TEST_ENV_VAR", "test-value");
        mockEnv.put("NESTED_VALUE", "nested");

        SystemEnvironmentPropertySource source = new SystemEnvironmentPropertySource("test", mockEnv);

        // 测试原始格式
        assertEquals("test-value", source.getProperty("TEST_ENV_VAR"));

        // 测试小写格式
        assertEquals("test-value", source.getProperty("test.env.var"));

        // 测试不存在的属性
        assertNull(source.getProperty("non.existent"));

        // 测试containsProperty方法
        assertTrue(source.containsProperty("test.env.var"));
        assertTrue(source.containsProperty("TEST_ENV_VAR"));
        assertFalse(source.containsProperty("non.existent"));
    }

    /**
     * 测试Environment合并
     */
    @Test
    public void testEnvironmentMerge() {
        // 创建父环境
        StandardEnvironment parent = new StandardEnvironment();
        parent.setActiveProfiles("parent");

        // 添加自定义属性源到父环境
        MutablePropertySources parentSources = parent.getPropertySources();
        parentSources.addFirst(new MapPropertySource("parentProperties",
                java.util.Collections.singletonMap("parent.property", "parent-value")));

        // 创建子环境
        StandardEnvironment child = new StandardEnvironment();
        child.setActiveProfiles("child");

        // 添加自定义属性源到子环境
        MutablePropertySources childSources = child.getPropertySources();
        childSources.addFirst(new MapPropertySource("childProperties",
                java.util.Collections.singletonMap("child.property", "child-value")));

        // 合并父环境到子环境
        child.merge(parent);

        // 测试合并后的profiles
        assertTrue(child.acceptsProfiles("parent"));
        assertTrue(child.acceptsProfiles("child"));

        // 测试合并后的属性
        assertEquals("child-value", child.getProperty("child.property"));
        assertEquals("parent-value", child.getProperty("parent.property"));
    }

}
