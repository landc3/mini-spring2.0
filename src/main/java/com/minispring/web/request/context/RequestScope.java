package com.minispring.web.request.context;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.ObjectFactory;
import com.minispring.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求作用域实现
 * Bean的生命周期与HTTP请求相同，一个请求内共享一个Bean实例
 */
public class RequestScope implements Scope {

    /**
     * 它定义了一个私有的、不可变的 ThreadLocal 变量 requestScope。
     * 对于每一个访问它的线程，如果这是该线程首次访问，则会通过 HashMap::new 创建一个新的 HashMap 实例作为该线程的初始值。
     * 这使得每个线程都能拥有自己独立的 Map<String, Object> 来存储请求范围内的数据，比如用户认证信息、请求参数等。
     */
    private final ThreadLocal<Map<String, Object>> requestScope = ThreadLocal.withInitial(HashMap::new);
    // 作用域销毁回调
    private final ThreadLocal<Map<String, Runnable> > destructionCallbacks = ThreadLocal.withInitial(HashMap::new);


    /**
     * 获取作用域数据
     * @param name Bean名称
     * @param objectFactory Bean不存在时，创建Bean的工厂
     * @return Bean实例
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        //从 ThreadLocal 变量 requestScope 中获取当前线程的 Map<String, Object> 实例，并将其赋值给局部变量 scope。
        Map<String, Object> scope = this.requestScope.get();
        //从当前线程的 Map<String, Object> 实例（即 scope）中，根据键 name 获取对应的对象，并将其赋值给变量 bean
        Object bean = scope.get(name);
        if (bean == null) {
            try {
                bean = objectFactory.getObject();
                scope.put(name, bean);
            } catch (BeansException ex) {
                throw ex;
            }
        }

        return bean;
    }

    /**
     * 移除作用域数据
     * @param name Bean名称
     * @return 移除的Bean实例
     */
    @Override
    public Object remove(String name) {
        Map<String, Object> scope = this.requestScope.get();
        return scope.remove(name);
    }
    /**
     * 注册作用域销毁回调
     * @param name Bean名称
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // 注册销毁回调
        this.destructionCallbacks.get().put(name, callback);
    }
    /**
     * 获取会话ID
     * @return 会话ID
     */
    @Override
    public String getConversationId() {
        return "request-" + Thread.currentThread().getName();

    }
    /**
     * 执行请求结束回调
     * 通常在HTTP请求结束时被调用
     */
public void destroyRequestScope() {
        // 获取当前线程的 Map<String, Runnable> 实例，并将其赋值给局部变量 callbacks。
        Map<String, Runnable> callbacks = this.destructionCallbacks.get();
        // 遍历 callbacks 中的所有键值对，执行对应的销毁回调。
        for (Map.Entry<String, Runnable> entry : callbacks.entrySet()) {
            try {
                entry.getValue().run();
            }
            catch (Throwable ex) {
                System.err.println("Exception thrown while executing destruction callback for request bean [" + entry.getKey() + "]: " + ex);
            }
            }
        //  清空ThreadLocal资源
        this.requestScope.remove();
        this.destructionCallbacks.remove();
        }
    }




