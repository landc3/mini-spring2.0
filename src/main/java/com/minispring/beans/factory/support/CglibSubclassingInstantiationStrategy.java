package com.minispring.beans.factory.support;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Constructor;

/**
 * CGLIB子类化实例化策略
 * 使用CGLIB动态生成子类来实例化Bean，适用于没有默认构造函数的类
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy{
    /**
     * 使用CGLIB动态生成子类来实例化Bean
     * @param beanDefinition Bean定义
     * @param beanName Bean名称
     * @param constructor 构造函数
     * @param args 构造函数参数
     * @return 实例化的Bean对象
     * @throws BeansException 如果实例化过程中发生异常
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?> constructor, Object[] args) throws BeansException {
        try {
            //创建cglib增强
            Enhancer enhancer = new Enhancer();
            //设置父类为beanDefinition的beanClass
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            //设置回调函数，这里使用NoOpCallback，即不设置任何拦截，和父类行为一样
            enhancer.setCallback(NoOp.INSTANCE);

            if (constructor == null) {
                //如果没有构造函数，则使用默认构造函数
                return enhancer.create();
            }
            if (args == null) {
                //参数为空，则创建一个空数组
                args = new Object[0];
            }
            //检查参数的个数是否与构造函数的参数个数匹配
            if (args.length != constructor.getParameterCount()) {
                throw new BeansException("参数个数不匹配");
            }
            //使用指定的构造函数创建实例
            return enhancer.create(constructor.getParameterTypes(), args);
        }catch (Exception e){
            throw new BeansException("使用cglib实例化Bean失败 [" + beanName + "]", e);
        }
    }
}
