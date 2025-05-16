package com.minispring.web.request.context;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.ObjectFactory;
import com.minispring.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP会话作用域实现
 * Bean的生命周期与HTTP会话相同，一个会话内共享一个Bean实例
 */
public class SessionScope implements Scope {

    // 使用sessionId作为key，对应的Bean实例集合作为value
    private  final Map<String,Map<String, Object>> sessionBeanMap = new ConcurrentHashMap();
    //  销毁回调映射
    private final Map<String, Map<String, Runnable>> destructionCallbacks = new ConcurrentHashMap();

    //用于获取当前会话id的接口，可以由外部设置
    private SessionIdResolver sessionIdResolver = () -> "default-session";

    /**
     * 设置会话ID解析器
     * @param sessionIdResolver 会话ID解析器
     */
    public void setSessionIdResolver(SessionIdResolver sessionIdResolver) {
        this.sessionIdResolver = sessionIdResolver;
    }

    /**
     * 获取指定名称的bean实例
     * @param name Bean名称
     * @param objectFactory 如果无法找到指定名称的Bean实例，则创建一个
     * return Bean实例
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        //获取当前会话ID
        String sessionId = this.sessionIdResolver.resolveSessionId();
        // 从会话映射中获取指定会话ID的Bean实例集合
        Map<String, Object> sessionMap = this.sessionBeanMap.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>(8));

        Object bean = sessionMap.get(name);
        if (bean == null) {
            try {
                bean = objectFactory.getObject();
                sessionMap.put(name, bean);
            } catch (BeansException ex) {
                throw ex;
            }
        }
        return bean;
    }

    /**
     * 移除指定名称的bean实例
     * @param name Bean名称
     * return 移除的Bean实例
     */
    @Override
    public Object remove(String name) {
        String sessionId = this.sessionIdResolver.resolveSessionId();
        Map<String, Object> sessionMap = this.sessionBeanMap.get(sessionId);
        if (sessionMap != null) {
            return sessionMap.remove(name);
        }
        return null;
    }

    /**
     * 注册销毁回调
     * @param name Bean名称
     * @param callback 销毁回调
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
       //获取当前会话ID
        String sessionId = this.sessionIdResolver.resolveSessionId();
        //从会话映射中获取指定会话ID的销毁回调集合·
        Map<String, Runnable> callbacks = this.destructionCallbacks.computeIfAbsent(sessionId, k -> new HashMap<>(8));
        callbacks.put(name, callback);
    }
    /**
     * 获取会话ID
     * @return 会话ID
     */
    @Override
    public String getConversationId() {
        return "session-" + sessionIdResolver.resolveSessionId();
    }
    /**
     * 执行会话结束回调
     * 通常在HTTP会话结束时被调用
     * @param sessionId 会话ID
     */
    public void endSession(String sessionId) {
        // 执行销毁回调
        Map<String, Runnable> callbacks = this.destructionCallbacks.remove(sessionId);
        if (callbacks != null) {
            for (Map.Entry<String, Runnable> entry : callbacks.entrySet()) {
                try {
                    entry.getValue().run();
                }
                catch (Throwable ex) {
                    System.err.println("Exception thrown while executing destruction callback for session bean [" + entry.getKey() + "]: " + ex);
                }
            }
        }

        // 移除会话中的所有Bean
        this.sessionBeanMap.remove(sessionId);
    }



    /**
     * 会话ID解析器接口
     * 用于获取当前会话ID
     */
    @FunctionalInterface//  函数式接口
    public interface SessionIdResolver {
        /**
         * 解析当前会话ID
         * @return 当前会话ID
         */
        String resolveSessionId();
    }
}
