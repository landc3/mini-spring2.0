package com.minispring.beans.factory.support;

import com.minispring.beans.*;

import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.BeanNameAware;
import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.config.BeanReference;
//import com.minispring.beans.factory.config.BeanPostProcessor;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 抽象自动装配Bean工厂
 * 实现创建Bean的功能
 */
/**
 * AbstractAutowireCapableBeanFactory
 * 是一个抽象类，提供了具体的 Bean 创建和管理逻辑。
 * 是 Spring IoC 容器的核心实现之一，负责完成 Bean 的实例化、属性填充、初始化和销毁等完整流程。
 * 它提供了具体的 Bean 创建逻辑，支持自动装配（autowiring）和依赖注入（dependency injection）。
 * 支持循环依赖：使用三级缓存机制解决循环依赖问题。
 * 实例化策略：提供了 InstantiationStrategy 接口，允许使用不同的实例化策略（如 CGLIB 或 JDK 动态代理）。
 * 内部实现了 AutowireCapableBeanFactory 接口的功能。
 *
 * AutowireCapableBeanFactory
 * 是一个接口，定义了自动装配和 Bean 生命周期管理的标准。
 * 提供给开发者手动控制 Bean 的装配和生命周期的能力。
 * 它的实现通常由 AbstractAutowireCapableBeanFactory 或其子类完成。
 * 联系与协作
 * AutowireCapableBeanFactory 提供了高层次的功能规范，而 AbstractAutowireCapableBeanFactory 提供了具体的实现。
 * 它们共同构成了 Spring IoC 容器的核心功能，支持灵活的 Bean 管理和装配。
 */

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 实例化策略
     *
     */
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();
    /**
     * 设置实例化策略
     */
    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
    /**
     * 获取实例化策略
     */
    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }
    /**
     * 实现 创建Bean实例功能
     * @param beanName Bean名称
     * @param beanDefinition Bean定义
     * @Param args 构造函数入参
     * @return 创建的Bean实例
     * @throws BeansException
     */
//    @Override
//    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
//        Object bean = null;
//        try {
//            // 1. 创建Bean实例
//            bean = createBeanInstance(beanDefinition, beanName, args);
//            // 2. 为Bean对象填充属性
//            applyPropertyValues(beanName, bean, beanDefinition);
//            // 3. 执行Bean的初始化方法和BeanPostProcessor的前置和后置处理方法
//            bean = initializeBean(beanName, bean, beanDefinition);
//        }catch (Exception e){
//            throw new BeansException("Instantiation of bean failed："+beanName, e);
//        }
//        // 4. 注册单例对象
//        if (beanDefinition.isSingleton()){
//            registerSingleton(beanName, bean);
//        }
//
//        return bean;
//    }
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            // 创建Bean实例
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 处理循环依赖，将实例化后的Bean对象提前放入三级缓存
            // 只有单例且允许循环依赖的Bean才会进行提前暴露
            if (beanDefinition.isSingleton()) {
                final Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
                System.out.println("将Bean[" + beanName + "]提前曝光到三级缓存");
            }

            // 创建Bean包装器
            BeanWrapper beanWrapper = new BeanWrapper(bean);

            // 填充Bean属性
            applyPropertyValues(beanName, bean, beanDefinition, beanWrapper);

            // 执行Bean的初始化方法和BeanPostProcessor的前置和后置处理
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("创建Bean失败: " + beanName, e);
        }

        // 注册销毁方法回调
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 注册单例Bean
        if (beanDefinition.isSingleton()) {
            // 处理FactoryBean和循环依赖后，最终加入到单例缓存
            // 如果这个Bean被提前暴露过（即解决了循环依赖），这一步会清除三级缓存中的工厂对象
            registerSingleton(beanName, bean);
        }

        return bean;
    }


    /**
     * 创建Bean实例
     * @param beanDefinition Bean定义
     * @param beanName Bean名称
     * @param args 构造参数
     * @return Bean实例
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor<?> constructorToUse = null; // 声明一个变量，用于存储要使用的构造函数
        Class<?> beanClass = beanDefinition.getBeanClass();// 获取Bean的Class对象
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();// 获取Bean的构造函数列表

        // 如果有构造参数，则查找匹配的构造函数
        if (args != null && args.length > 0) {
            for (Constructor<?> ctor : declaredConstructors) {
                if (ctor.getParameterTypes().length == args.length) {
                    constructorToUse = ctor;
                    break;
                }
            }
        } else {
            try {
                // 如果没有构造参数，则使用默认构造函数，即获取无参构造函数
                return beanClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new BeansException("创建Bean实例失败: " + beanName, e);
            }
        }

        // 使用找到的构造函数创建实例
        try {
            return constructorToUse.newInstance(args);
        } catch (Exception e) {
            throw new BeansException("创建Bean实例失败: " + beanName, e);
        }
    }
    /**
     * 为Bean设置属性值
     *
     * @param beanName       Bean名称
     * @param bean           Bean实例
     * @param beanDefinition Bean定义
     * @param beanWrapper
     * 下面被注释的代码被注释的代码会报错，创建Bean[testBean]失败
     */
//    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
//        try {
//            PropertyValues propertyValues = beanDefinition.getPropertyValues();
//            if (propertyValues.isEmpty()) {
//                return;
//            }
//
//            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
//                String name = propertyValue.getName();
//                Object value = propertyValue.getValue();
//
//                // 优先使用转换后的值
//                Object valueToUse = propertyValue.getConvertedValue();
//                if (valueToUse == null) {
//                    valueToUse = value;
//                }
//
//                // 通过反射设置属性值
//                Field field = bean.getClass().getDeclaredField(name);
//                field.setAccessible(true);
//                field.set(bean, valueToUse);
//            }
//        } catch (Exception e) {
//            throw new BeansException("填充Bean属性失败: " + beanName, e);
//        }
//    }
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            if (propertyValues.isEmpty()) {
                return;
            }

            // 创建类型转换器
            TypeConverter typeConverter = new SimpleTypeConverter();

            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                // 处理Bean引用
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }

                // 使用BeanWrapper设置属性值
                beanWrapper.setPropertyValue(name, value);
            }
        } catch (Exception e) {
            throw new BeansException("填充Bean属性失败: " + beanName, e);
        }
    }

    /**
     * 初始化Bean
     * @param beanName Bean名称
     * @param bean Bean实例
     * @param beanDefinition Bean定义
     * @return 初始化后的Bean实例
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        //处理aware接口
        if (bean instanceof BeanFactoryAware) {
            //设置BeanFactory，使得Aware接口的实现类可以获取BeanFactory，从而可以访问BeanFactory中的方法
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        if (bean instanceof BeanNameAware){
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        // 执行BeanPostProcessor的前置处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        // 执行初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("执行Bean初始化方法失败: " + beanName, e);
        }
        // 执行BeanPostProcessor的后置处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    /**
     * 执行Bean的初始化方法
     * @param beanName Bean名称
     * @param bean Bean实例
     * @param beanDefinition Bean定义
     */
/*    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 如果Bean定义了初始化方法，则执行
        String initMethodName = beanDefinition.getInitMethodName();
        if (initMethodName != null && !initMethodName.isEmpty()) {
            try {
                // 通过反射执行初始化方法
                bean.getClass().getMethod(initMethodName).invoke(bean);
            } catch (Exception e) {
                throw new BeansException("执行Bean初始化方法失败: " + beanName, e);
            }
        }
    }*/
    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
      //1.如果Bean实现了InitializingBean接口，则执行afterPropertiesSet方法
        if (bean instanceof InitializingBean){
            ((InitializingBean) bean).afterPropertiesSet();
            System.out.println("执行Bean[" + beanName + "]的InitializingBean接口的afterPropertiesSet方法");
        }
        //2.如果Bean定义了初始化方法，则执行
        String initMethodName = beanDefinition.getInitMethodName();
        if (initMethodName != null && !initMethodName.isEmpty() && !(bean instanceof InitializingBean && "afterPropertiesSet".equals(initMethodName)) ){
            // 通过反射执行初始化方法
            try {
                bean.getClass().getMethod(initMethodName).invoke(bean);
                System.out.println("执行Bean[" + beanName + "]的自定义初始化方法：" + initMethodName);
            } catch (Exception e) {
                throw new BeansException("找不到Bean[" + beanName + "]的初始化方法：" + initMethodName, e);
            }

        }
    }

    /**
     * 执行BeanPostProcessor的前置处理
     * @param existingBean 现有的Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    /**
     * 功能：
     * 遍历所有注册的 BeanPostProcessor 实例，并依次调用它们的 postProcessBeforeInitialization 方法。
     * 这是一个前置处理方法，在 Bean 初始化（如调用 @PostConstruct 或 InitializingBean.afterPropertiesSet()）之前执行。
     *
     * BeanPostProcessor 是 Spring 提供的一个扩展点，允许开发者在 Bean 生命周期的不同阶段对其进行干预。
     *
     * current 是当前 BeanPostProcessor 的 postProcessBeforeInitialization 方法返回的结果。
     * result 是经过上一个 BeanPostProcessor 处理后的 Bean 实例（初始值是传入的 existingBean）。
     * 这段代码的作用是：
     * 检查 current 是否为 null。
     * 如果 current 为 null，直接返回当前的 result，不再继续调用后续的 BeanPostProcessor。
     * 如果 current 不为 null，将 current 赋值给 result，作为下一个 BeanPostProcessor 的输入。
     */
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        //// 遍历BeanPostProcessor并执行前置处理
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 调用BeanPostProcessor的前置处理方法
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 执行BeanPostProcessor的后置处理
     * @param existingBean 现有的Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 注册销毁方法回调
     * @param beanName Bean名称
     * @param bean Bean实例
     * @param beanDefinition Bean定义
     */
    /**
     * 方法的作用
     * 核心目标
     * 注册销毁方法：
     * 当 Spring 容器关闭时，Spring 需要调用某些 Bean 的销毁逻辑（例如释放资源、清理内存等）。
     * 这个方法的作用是检查当前 Bean 是否需要销毁，并为其注册销毁逻辑。
     * 适用场景
     * 单例 Bean：
     * 只有单例 Bean 才需要注册销毁方法，因为它们由 Spring 容器管理生命周期。
     * 对于原型（Prototype）Bean，Spring 不会管理其销毁逻辑，因此无需注册。
     * 两种销毁方式：
     * 如果 Bean 实现了 DisposableBean 接口，则调用其 destroy() 方法。
     * 如果 Bean 配置了自定义销毁方法（通过 XML 配置或注解指定），则通过反射调用该方法。
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 只有单例Bean才需要注册销毁方法
        if (!beanDefinition.isSingleton()) {
            return;
        }

        if (bean instanceof DisposableBean ||
                // 判断Bean是否实现了DisposableBean接口，或者定义了destroy方法
                (beanDefinition.getDestroyMethodName() != null && !beanDefinition.getDestroyMethodName().isEmpty())) {
            // 创建DisposableBeanAdapter并注册
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition.getDestroyMethodName()));
        }
    }

    /**
     * 获取早期Bean引用，用于解决循环依赖
     * 主要用于AOP场景，普通Bean直接返回原始对象，AOP则返回代理对象
     *
     * @param beanName Bean名称
     * @param beanDefinition Bean定义
     * @param bean Bean实例
     * @return 早期引用
     */
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        // 这里可以对Bean进行后续处理，比如创建代理对象等
        // 暂时简单实现，直接返回原始对象
        System.out.println("获取Bean[" + beanName + "]的早期引用");
        return exposedObject;
    }
}