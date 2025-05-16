package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.SimpleTypeConverter;
import com.minispring.beans.TypeConverter;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.DependencyDescriptor;
import com.minispring.core.DefaultParameterNameDiscoverer;
import com.minispring.core.ParameterNameDiscoverer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 构造函数解析器
 * 用于解析构造函数参数，并进行自动装配
 * 根据参数类型/数量选择最合适的构造函数，自动装配构造函数参数，处理循环依赖、参数不匹配等问题
 */
public class ConstructorResolver {
    private final AbstractAutowireCapableBeanFactory beanFactory;// bean工厂
    //类型转化
    private final TypeConverter typeConverter;
    //正在创建的bean名称集合
    private final Set<String>  inCreateBeans;
    //参数名发现器
    private final ParameterNameDiscoverer parameterNameDiscoverer;

    /**
     * 构造函数解析器
     * @param beanFactory bean工厂
     * @param typeConverter 类型转化器
     * @param inCreateBeans 正在创建的bean名称集合
     * @param parameterNameDiscoverer 参数名发现器
     */
    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory, TypeConverter typeConverter, Set<String> inCreateBeans, ParameterNameDiscoverer parameterNameDiscoverer) {
        this.beanFactory = beanFactory;
        this.typeConverter = new SimpleTypeConverter();
        this.inCreateBeans = new HashSet<>();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    /**
     * 检查是否存在循环依赖
     * @param beanName bean名称
     * @param dependencyType 依赖类型
     *
     */
    /**
     * beanName 参数：
     * 这是方法的输入参数，表示当前正在检查循环依赖的 Bean 的名称。
     * 它的作用是用来标识当前的 Bean，方便在检测到循环依赖时输出日志信息。
     * dependencyType 参数：
     * 表示当前 Bean 所依赖的类型（Class 类型）。
     * 方法会根据这个类型查找所有匹配的 Bean 名称。
     * dependencyNames：
     * 通过 factory.getBeanNamesForType(dependencyType) 获取的是 Spring 容器中所有与 dependencyType 类型匹配的 Bean 名称。
     * 这些名称可能包含一个或多个 Bean，具体取决于容器中有多少个 Bean 的类型与 dependencyType 匹配。
     * dependencyName：
     * 是 dependencyNames 数组中的每一个元素，表示与 dependencyType 匹配的某个 Bean 的名称。
     * 在循环中，方法会逐一检查这些 Bean 是否正在创建中（通过 beanFactory.isSingletonCurrentlyInCreation(dependencyName) 判断）。
     * isSingletonCurrentlyInCreation(dependencyName)：
     * 这个方法用于判断指定的 Bean 是否正在创建过程中。
     * 如果某个 dependencyName 正在创建中，并且与当前的 beanName 形成依赖关系，则认为存在循环依赖。
     */
    public boolean isCircularDependency(String beanName, Class<?> dependencyType) {
        /**
         * beanFactory 通常是通过依赖注入（@Autowired）或从 ApplicationContext 中获取的。
         * 它是由 Spring 容器创建并管理的，代表了当前应用上下文中的 Bean 工厂。
         */
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;//将beanFactory转换为DefaultListableBeanFactory
        //检查是否有匹配依赖类型的Bean正在创建中
        String[] beanNamesForType = factory.getBeanNamesForType(dependencyType);
        for (String dependencyName : beanNamesForType) {
            if (beanFactory.isSingletonCurrentlyInCreation(dependencyName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取早期Bean引用
     * 作用：为了解决循环依赖问题，只考虑单例Bean，无需考虑原型Bean
     * @param beanName bean名称
     *
     */
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
    public Object getEarlyBeanReference(String beanName) throws BeansException {
        //尝试从缓存中获取早期引用
        Object earlyReferenceBean = beanFactory.getSingleton(beanName);
        if (earlyReferenceBean != null){
            System.out.println("获取到Bean[" + beanName + "]的早期引用");
            return earlyReferenceBean;
        }
        System.out.println("无法获取Bean[" + beanName + "]的早期引用，可能是尚未进入创建流程");
        return null;
    }


    /**
     * 自动装配构造函数
     *
     * @param beanName Bean名称
     * @param beanDefinition Bean定义
     * @param constructors 候选构造函数
     * @param args 显式提供的参数
     * @return 解析后的构造函数和参数
     * @throws BeansException 如果无法解析构造函数
     */
    public BeanInstantiationContext autowireConstructor(String beanName, BeanDefinition beanDefinition,
                                                        Constructor<?>[] constructors, Object[] args) throws BeansException {

        System.out.println("开始解析构造函数: " + beanName + ", 构造函数数量: " + (constructors != null ? constructors.length : 0));

        // 如果没有提供构造函数，使用默认构造函数
        if (constructors == null || constructors.length == 0) {
            try {
                Constructor<?> defaultCtor = beanDefinition.getBeanClass().getDeclaredConstructor();
                System.out.println("使用默认构造函数: " + defaultCtor);
                return new BeanInstantiationContext(defaultCtor, new Object[0]);
            } catch (NoSuchMethodException e) {
                throw new BeansException("无法找到默认构造函数: " + beanDefinition.getBeanClass().getName(), e);
            }
        }

        // 如果提供了参数，根据参数类型匹配构造函数
        if (args != null && args.length > 0) {
            System.out.println("提供了参数，尝试匹配构造函数: " + Arrays.toString(args));
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == args.length) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    boolean match = true;
                    Object[] convertedArgs = new Object[args.length];

                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null) {
                            // 尝试进行类型转换
                            if (!paramTypes[i].isInstance(args[i])) {
                                try {
                                    convertedArgs[i] = typeConverter.convertIfNecessary(args[i], paramTypes[i]);
                                } catch (Exception e) {
                                    match = false;
                                    break;
                                }
                            } else {
                                convertedArgs[i] = args[i];
                            }
                        }
                    }

                    if (match) {
                        System.out.println("找到匹配的构造函数: " + constructor);
                        return new BeanInstantiationContext(constructor, convertedArgs);
                    }
                }
            }
        }

        // 根据参数数量排序构造函数（优先使用参数较多的构造函数）
        Arrays.sort(constructors, (c1, c2) -> c2.getParameterCount() - c1.getParameterCount());

        // 尝试找到可以自动装配的构造函数
        for (Constructor<?> constructor : constructors) {
            System.out.println("尝试自动装配构造函数: " + constructor);

            try {
                // 获取参数名称
                String[] paramNames = parameterNameDiscoverer.getParameterNames(constructor);
                Parameter[] parameters = constructor.getParameters();
                Object[] resolvedArgs = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    String paramName = paramNames != null ? paramNames[i] : parameters[i].getName();
                    Class<?> paramType = parameters[i].getType();

                    // 检查循环依赖
                    if (isCircularDependency(beanName, paramType)) {
                        resolvedArgs[i] = getEarlyBeanReference(beanName);
                        continue;
                    }

                    // 创建依赖描述符
                    DependencyDescriptor descriptor = new DependencyDescriptor(parameters[i], true);
                    descriptor.setParameterName(paramName);

                    try {
                        resolvedArgs[i] = resolveDependent(descriptor, beanName);
                    } catch (BeansException e) {
                        System.out.println("无法解析参数: " + paramName + ", 类型: " + paramType.getName());
                        throw e;
                    }
                }

                System.out.println("成功解析构造函数参数: " + Arrays.toString(resolvedArgs));
                return new BeanInstantiationContext(constructor, resolvedArgs);

            } catch (BeansException e) {
                System.out.println("自动装配构造函数失败: " + e.getMessage());
                // 继续尝试下一个构造函数
            }
        }

        // 如果没有找到合适的构造函数，尝试使用默认构造函数
        try {
            Constructor<?> defaultCtor = beanDefinition.getBeanClass().getDeclaredConstructor();
            System.out.println("没有找到合适的构造函数，使用默认构造函数: " + defaultCtor);
            return new BeanInstantiationContext(defaultCtor, new Object[0]);
        } catch (NoSuchMethodException e) {
            throw new BeansException("无法找到合适的构造函数: " + beanDefinition.getBeanClass().getName(), e);
        }
    }


    /**
     * 解析依赖
     *
     * @param descriptor 依赖描述符
     * @param beanName 当前Bean名称
     * @return 解析后的依赖对象
     * @throws BeansException 如果无法解析依赖
     */
    private Object resolveDependent(DependencyDescriptor descriptor, String beanName) throws BeansException {
        Class<?> type = descriptor.getDependencyType();
        String dependencyName = descriptor.getDependencyName();

        System.out.println("解析依赖: 类型=" + type.getName() + ", 名称=" + dependencyName);

        // 如果指定了依赖名称，直接获取
        if (dependencyName != null && !dependencyName.isEmpty()) {
            System.out.println("按名称获取Bean: " + dependencyName);
            try {
                return beanFactory.getBean(dependencyName);
            } catch (BeansException e) {
                // 如果按名称获取失败，继续尝试其他方式
                System.out.println("按名称获取Bean失败: " + e.getMessage());
            }
        }

        // 尝试按类型查找
        try {
            System.out.println("按类型获取Bean: " + type.getName());
            DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            String[] beanNames = listableBeanFactory.getBeanNamesForType(type);

            if (beanNames.length == 1) {
                String autowiredBeanName = beanNames[0];
                System.out.println("找到唯一匹配的Bean: " + autowiredBeanName);
                return beanFactory.getBean(autowiredBeanName);
            } else if (beanNames.length > 1) {
                // 如果找到多个匹配的Bean，按以下优先级尝试：
                // 1. 使用参数名称
                // 2. 使用依赖名称（如果有）
                // 3. 使用类型名称（首字母小写）
                String paramName = descriptor.getMethodParameter() != null ?
                        descriptor.getMethodParameter().getName() : null;

                // 1. 尝试使用参数名称
                if (paramName != null && Arrays.asList(beanNames).contains(paramName)) {
                    System.out.println("根据参数名称找到匹配的Bean: " + paramName);
                    return beanFactory.getBean(paramName);
                }

                // 2. 尝试使用依赖名称
                if (dependencyName != null && Arrays.asList(beanNames).contains(dependencyName)) {
                    System.out.println("根据依赖名称找到匹配的Bean: " + dependencyName);
                    return beanFactory.getBean(dependencyName);
                }

                // 3. 尝试使用类型名称（首字母小写）
                String typeNameBean = type.getSimpleName().substring(0, 1).toLowerCase() +
                        type.getSimpleName().substring(1);
                if (Arrays.asList(beanNames).contains(typeNameBean)) {
                    System.out.println("根据类型名称找到匹配的Bean: " + typeNameBean);
                    return beanFactory.getBean(typeNameBean);
                }

                // 如果都没有找到匹配的，抛出异常
                throw new BeansException("找到多个类型为 '" + type.getName() + "' 的Bean: " +
                        String.join(", ", beanNames));
            }

            // 如果没有找到匹配的Bean，尝试使用类型名称（首字母小写）
            String typeNameBean = type.getSimpleName().substring(0, 1).toLowerCase() +
                    type.getSimpleName().substring(1);
            if (beanFactory.containsBean(typeNameBean)) {
                System.out.println("使用类型名称找到Bean: " + typeNameBean);
                return beanFactory.getBean(typeNameBean);
            }

            throw new BeansException("找不到类型为 '" + type.getName() + "' 的Bean");

        } catch (BeansException e) {
            if (descriptor.isRequired()) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Bean实例化上下文
     * 包含构造函数 参数
     */
    public static class BeanInstantiationContext {
        private final Constructor<?> constructor;//构造函数
        private final Object[] args;//参数

        public BeanInstantiationContext(Constructor<?> constructor, Object[] args) {
            this.constructor = constructor;
            this.args = args;
        }

        public Constructor<?> getConstructor() {
            return constructor;
        }

        public Object[] getArgs() {
            return args;
        }
    }

}
