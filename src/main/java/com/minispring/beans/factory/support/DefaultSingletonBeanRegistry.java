package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;

import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.ObjectFactory;
import com.minispring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认单例Bean注册表实现
 * 实现SingletonBeanRegistry接口，提供单例Bean的注册和获取功能
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /** 一级缓存：完全初始化好的单例对象缓存 */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    
    /** 二级缓存：提前曝光的单例对象（未完全初始化）缓存 */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    
    /** 三级缓存：单例工厂缓存，用于保存bean创建工厂，以便后面利用工厂为bean创建代理对象 */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    
    /** 正在创建中的单例Bean名称集合 */
    private final Set<String> singletonsCurrentlyInCreation = ConcurrentHashMap.newKeySet();
/*

    */
/**
     * 需要销毁的Bean容器
     * 使用LinkedHashMap保证销毁顺序与注册顺序相反
     * String: beanName
     * DisposableBean：需要销毁的Bean
     */

    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();

    /**
     * . 职责分离的设计原则
     * 在软件设计中，遵循单一职责原则（Single Responsibility Principle, SRP）是非常重要的。这意味着一个方法或类应该只负责一件事情。将查找和创建分开，可以让代码更加清晰、可维护性更高。
     *
     * 第一个方法：专注于查找单例 Bean。
     * 它的职责是检查缓存中是否存在某个单例 Bean，并返回结果。
     * 如果不存在，则不负责创建，而是直接返回 null。
     * 第二个方法：专注于查找并创建单例 Bean。
     * 它的职责是确保单例 Bean 存在。如果找不到，则通过工厂方法创建新的实例。
     *
     * 需要创建两个不同参数的方法原因：
     * （1）减少不必要的创建操作
     * 第一个方法只负责查找，不会触发任何创建逻辑。
     * 在某些场景下，我们只需要检查某个 Bean 是否存在，而不需要实际创建它。
     * 如果使用第二个方法，即使只是检查是否存在，也会触发工厂方法的调用，导致不必要的性能开销。
     * （2）避免重复创建
     * 第二个方法会尝试创建 Bean，但如果该 Bean 已经存在，则会导致重复的查找操作。
     * 使用第一个方法可以快速判断 Bean 是否已经存在，从而避免不必要的创建逻辑。
     * （3）在解决循环依赖的过程中，只需要查找单例 Bean，而不需要创建新的实例。
     * 如果使用第二个方法，可能会触发不必要的创建逻辑，导致循环依赖无法正确解决。
     *
     * 二者在使用中相辅相成：
     * （1）查找单例 Bean
     * 当容器启动时，首先尝试获取 A 和 B 的单例实例。
     * 如果缓存中没有找到，则调用第一个方法 getSingleton("A") 和 getSingleton("B")。
     * （2）创建单例 Bean
     * 如果缓存中没有找到 A 或 B，则调用第二个方法 getSingleton("A", factory) 和 getSingleton("B", factory)。
     * 在创建 A 的过程中，发现它依赖于 B，此时会再次调用 getSingleton("B")。
     * 如果 B 正在创建中，则从二三级缓存中获取提前暴露的 B 实例，从而解决循环依赖。
     * */

    /**
     * Spring 使用三级缓存来管理 Bean 的早期引用和完全初始化的实例：
     *
     * 一级缓存（Singleton Objects）：
     * 存储完全初始化的单例 Bean 实例。
     * 当 Bean 完成初始化后，会从二级或三级缓存中移除，并放入一级缓存。
     * 二级缓存（Early Singleton Objects）：
     * 存储早期引用（未完全初始化的 Bean 实例）。
     * 当 Bean 正在创建过程中，Spring 会将它的早期引用放入二级缓存。
     * 三级缓存（Singleton Factories）：
     * 存储用于创建早期引用的工厂对象（ObjectFactory）。
     * 当 Bean 需要早期引用但尚未创建时，Spring 会通过三级缓存中的工厂对象动态生成早期引用。
     */
    /**
     * 获取单例Bean
     * 实现三级缓存查找
     * 
     * @param beanName Bean名称
     * @return 单例Bean对象，如果不存在返回null
     */
    @Override
    public Object getSingleton(String beanName) {
        // 首先从一级缓存中获取
        Object singletonObject = singletonObjects.get(beanName);
        
        // 如果一级缓存中没有，并且该Bean正在创建中（可能存在循环依赖）
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) { //synchronized保证线程安全
                // 从二级缓存中获取
                singletonObject = earlySingletonObjects.get(beanName);
                
                // 如果二级缓存也没有，则尝试从三级缓存获取
                if (singletonObject == null) {
                    ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 通过工厂获取对象
                        singletonObject = singletonFactory.getObject();
                        // 放入二级缓存，并从三级缓存移除
                        earlySingletonObjects.put(beanName, singletonObject);
                        singletonFactories.remove(beanName);
                    }
                }
            }
        }
        
        return singletonObject;
    }
    
    /**
     * 获取单例Bean
     * 如果不存在则通过提供的ObjectFactory创建
     * 
     * @param beanName Bean名称
     * @param singletonFactory Bean工厂
     * @return 单例Bean
     */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            // 首先检查一级缓存
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                
                // 标记该Bean正在创建中
                beforeSingletonCreation(beanName);
                
                boolean newSingleton = false; // 是否新创建的标志
                try {
                    // 使用工厂创建单例
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true; // 标记是否新创建的
                } catch (Exception ex) {
                    throw new BeansException("创建单例Bean[" + beanName + "]失败", ex);
                } finally {
                    // 清除正在创建标记
                    afterSingletonCreation(beanName);
                }
                
                if (newSingleton) {
                    // 将创建好的单例加入一级缓存，并从二三级缓存移除
                    addSingleton(beanName, singletonObject);
                }
            }
            
            return singletonObject;
        }
    }
    
    /**
     * 添加单例Bean到一级缓存，并清除二三级缓存
     * 
     * @param beanName Bean名称
     * @param singletonObject 单例Bean
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
        }
    }
    
    /**
     * 添加单例工厂到三级缓存
     * 
     * @param beanName Bean名称
     * @param singletonFactory 单例工厂
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) { 
            if (!this.singletonObjects.containsKey(beanName)) { // 如果一级缓存中没有该Bean
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
            }
        }
    }

    /**
     * 注册单例Bean到一级缓存
     * 
     * @param beanName Bean名称
     * @param singletonObject 单例Bean
     */
    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName); // 二三级缓存移除
            this.earlySingletonObjects.remove(beanName);
        }
    }

    /**
     * 注册需要销毁的Bean
     *
     * @param beanName Bean名称
     * @param bean 需要销毁的Bean
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    /**
     * 销毁单例Bean
     * 按照注册的相反顺序销毁Bean
     */
    public void destroySingletons() {
        Set<String> beanNames = disposableBeans.keySet();// 获取所有需要销毁的Bean名称
        String[] disposableBeanNames = beanNames.toArray(new String[0]);// 转换为数组

        // 按照注册的相反顺序销毁Bean
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            String beanName = disposableBeanNames[i];
            // 获取并销毁Bean
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("销毁Bean[" + beanName + "]时发生异常", e);
            }
        }

        // 清空所有缓存
        this.singletonObjects.clear();
        this.earlySingletonObjects.clear();
        this.singletonFactories.clear();
        this.singletonsCurrentlyInCreation.clear();
    }
    
    /**
     * 当前Bean是否正在创建中
     * 
     * @param beanName Bean名称
     * @return 是否正在创建中
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName); // 返回是否正在创建中
    }
    
    /**
     * 标记指定的Bean正在创建中
     * 
     * @param beanName Bean名称
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) { // 如果已经存在，则抛出异常
            throw new BeansException("Bean[" + beanName + "]已经在创建中");
        }
    }
    
    /**
     * 标记指定的Bean创建完成
     * 
     * @param beanName Bean名称
     */
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new BeansException("Bean[" + beanName + "]不在创建中，无法结束创建过程");
        }
    }



    /**
     * 判断是否包含指定名称的单例Bean
     * @param beanName Bean名称
     * @return 是否包含
     */
    protected boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }

    /**
     * 获取所有单例Bean的名称
     * @return 单例Bean名称数组
     */
    protected String[] getSingletonNames() {
        return singletonObjects.keySet().toArray(new String[0]);
    }

    /**
     * 获取单例Bean的数量
     * @return 单例Bean数量
     */
    protected int getSingletonCount() {
        return singletonObjects.size();
    }
}