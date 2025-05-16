package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.BeanFactory;

import com.minispring.beans.factory.ConfigurableListableBeanFactory;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.Scope;
import com.minispring.core.env.MapPropertySource;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的可列表Bean工厂实现
 * 支持单例和原型Bean，并提供Bean定义注册功能
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {


    /**
     * BeanDefinition容器
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 父BeanFactory
     */
    private BeanFactory parentBeanFactory;

    /**
     * 作用域容器
     */
    private final Map<String, Scope> scopes = new ConcurrentHashMap<>(8);

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("找不到名为 '" + beanName + "' 的BeanDefinition");
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 根据类型获取Bean名称
     * @param type Bean类型
     * @return Bean名称数组
     */
    public String[] getBeanNamesForType(Class<?> type) {
        return beanDefinitionMap.entrySet().stream()
                .filter(entry -> type.isAssignableFrom(entry.getValue().getBeanClass()))
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }

    /**
     * 根据类型获取Bean实例
     * @param requiredType Bean类型
     * @param <T> Bean类型
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        String[] beanNames = getBeanNamesForType(requiredType);//  获取指定类型的Bean名称
        if (beanNames.length == 0) {
            throw new BeansException("找不到类型为 '" + requiredType.getName() + "' 的Bean");
        }
        if (beanNames.length > 1) {
            throw new BeansException("找到多个类型为 '" + requiredType.getName() + "' 的Bean: " + String.join(", ", beanNames));
        }
        return getBean(beanNames[0], requiredType);
    }

    /**
     * 根据类型获取Bean实例
     * @param type Bean类型
     * @param <T> Bean类型
     * @return Bean实例
     * @throws BeansException 如果获取Bean失败
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanClass = beanDefinition.getBeanClass();

            if (type.isAssignableFrom(beanClass)) {
                T bean = (T) getBean(beanName);
                result.put(beanName, bean);
            }
        }
        return result;
    }

    /**
     * 预初始化所有非懒加载的单例Bean
     *
     * @throws BeansException 如果预初始化Bean失败
     */
    @Override
    public void preInstantiateSingletons() throws BeansException {
        // 预初始化所有非懒加载的单例Bean
        for (String beanName : getBeanDefinitionNames()) {// 遍历Bean定义
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if (beanDefinition.isSingleton()) {
                getBean(beanName);//  预初始化单例Bean
            }
        }
    }
    /**
     * 自动装配Bean
     *
     * @param existingBean 已存在的Bean实例
     * @param beanName Bean名称
     * @throws BeansException 如果自动装配失败
     */
    @Override
    public void autowireBean(Object existingBean, String beanName) throws BeansException {
        // 简单实现，实际自动装配更复杂
        System.out.println("自动装配Bean: " + beanName);
    }

    /**
     * 创建一个新的Bean实例
     *
     * @param beanClass Bean类
     * @return 新的Bean实例
     * @throws BeansException 如果创建Bean实例失败
     */
    @Override
    public Object createBean(Class<?> beanClass) throws BeansException {
        // 简单实现，创建一个新的Bean实例
        try {
            return beanClass.newInstance();
        } catch (Exception e) {
            throw new BeansException("创建Bean实例失败，类型：" + beanClass.getName(), e);
        }
    }
    /**
     * 获取父BeanFactory
     *
     * @return 父BeanFactory
     */
    @Override
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    /**
     * 设置父BeanFactory
     *
     * @param parentBeanFactory 父BeanFactory
     */
    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    /**
     * 判断当前BeanFactory是否包含指定名称的Bean定义
     *
     * @param name Bean名称
     * @return true表示包含，false表示不包含
     */
    @Override
    public boolean containsLocalBean(String name) {
        return containsBeanDefinition(name);
    }
    /**
     * 获取指定名称的Bean的类型
     *
     * @param name Bean名称
     * @return Bean的类型
     * @throws BeansException 如果获取Bean类型失败
     */
    @Override
    public Class<?> getType(String name) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition != null) {
            return beanDefinition.getBeanClass();// 返回Bean的类型
        }

        // 如果在当前工厂中找不到，尝试从父工厂获取
        if (this.parentBeanFactory != null) {
            if (this.parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                return ((ConfigurableListableBeanFactory) this.parentBeanFactory).getType(name);
            }
        }

        throw new BeansException("找不到名为 '" + name + "' 的Bean定义");
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {
        this.scopes.put(scopeName, scope);
    }
    /**
     * 获取指定名称的Bean的Scope
     *
     * @param scopeName Scope名称
     * @return Scope实例
     */
    @Override
    public Scope getRegisteredScope(String scopeName) {
        return this.scopes.get(scopeName);
    }



}