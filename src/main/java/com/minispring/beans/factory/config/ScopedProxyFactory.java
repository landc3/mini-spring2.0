package com.minispring.beans.factory.config;

import com.minispring.aop.framework.ProxyFactory;
import com.minispring.aop.framework.TargetSource;
import com.minispring.beans.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.ConfigurableBeanFactory;
import com.minispring.beans.factory.ObjectFactory;

/**
 * 作用域代理工厂
 * 用于创建作用域Bean的代理
 */
public class ScopedProxyFactory {
    /**
     * 创建作用域代理
     * @param targetBean 目标Bean
     * @param targetBeanName 目标Bean的名称
     * @param scopeName 作用域名称
     * @param beanFactory Bean工厂
     * return 作用域Bean的代理
     */
public static Object createScopedProxy(Object targetBean, String targetBeanName, String scopeName, ConfigurableBeanFactory beanFactory) {
        if (targetBean==null){
            //
            throw new IllegalArgumentException("targetBean must not be null");
        }
        //创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        //设置目标源为延迟目标源，每次获取代理对象时都会创建新的目标对象
//        proxyFactory.setTargetSource(new LazyTargetSource(targetBean.getClass(), targetBeanName, beanFactory));
        proxyFactory.setTargetSource(new ScopedTargetSource(targetBeanName, scopeName, beanFactory));
        //设置代理目标类
        if (targetBean.getClass().getInterfaces().length>0){
            //实现了接口，则使用JDK动态代理
            /**
             * 是否使用 JDK 动态代理或 CGLIB 代理，是由目标类是否实现了接口决定的，Spring 内部会自动选择合适的代理技术。
             * 选择的逻辑封装在  ProxyFactory 类的 createAopProxy() 方法中，该方法会根据目标类是否实现了接口来选择使用 JDK 动态代理或 CGLIB 代理。
             */
            proxyFactory.setTargetClass(targetBean.getClass());
        }else {
            //否则使用CGLIB代理
            proxyFactory.setTargetClass(targetBean.getClass());
        }
        //获取代理
        return proxyFactory.getProxy();
        }


        /**
         * 作用域目标源
         */
    private static class ScopedTargetSource implements TargetSource {
        private final String targetBeanName;//目标Bean的名称
        private final String scopeName;//作用域名称
        private final ConfigurableBeanFactory beanFactory;//Bean工厂


        public ScopedTargetSource(String targetBeanName, String scopeName, ConfigurableBeanFactory beanFactory) {
            this.targetBeanName = targetBeanName;
            this.scopeName = scopeName;
            this.beanFactory = beanFactory;
        }

        /**
         * 获取目标类的类型
         * @return 目标类的类型
         */
        @Override
        public Class<?> getTargetClass() {
            try {
                return beanFactory.getBean(targetBeanName).getClass();
            }catch (Exception e){
                // 如果无法获取类型，返回Object.class
                return Object.class;
            }
        }

        /**
         * 是否为静态目标源
         * @return 是否为静态目标源
         */
        @Override
        public boolean isStatic() {
            // 返回false，表示目标对象不是静态的，每次调用都需要解析
            return false;
        }

        /**
         * 获取目标对象
         * @return 目标对象
         * @throws Exception 获取目标对象时发生异常
         */
        @Override
        public Object getTarget() throws Exception {
            //获取作用域
            Scope scope = beanFactory.getRegisteredScope(this.scopeName);
            //从作用域中获取目标Bean
            if (scope == null) {
                throw new IllegalStateException("No Scope registered for scope name '" + this.scopeName + "'");
            }
            //创建对象工厂
            ObjectFactory<?> objectFactory = new ObjectFactory<Object>() {
                @Override
                public Object getObject() throws BeansException {
                    return beanFactory.getBean(targetBeanName);
                }
            };
            //使用ObjectFactory从作用域中获取目标Bean
            return scope.get(targetBeanName, objectFactory);

        }

        /**
         * 释放目标对象
         * @param target 目标对象
         * @throws Exception 释放目标对象时发生异常
         */
        @Override
        public void releaseTarget(Object target) throws Exception {
            // 不需要特别处理，作用域会自行管理对象的生命周期
        }
    }

}
