package com.minispring.beans.factory.config;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 单例作用域实现
 * Bean默认的作用域，整个应用只有一个Bean实例
 */
public class SingletonScope implements Scope{
    //  单例作用域的存储容器，用于存储单例对象
   private  final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    // 销毁回调缓存
    private final Map<String, Runnable> destructionCallbacks = new HashMap<>(16);

    /**
     * 获取单例对象
     * @param name Bean名称
     * @param objectFactory 如果单例对象不存在，则调用该工厂方法创建对象
     * return 单例对象
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        // 从一级缓存中获取
        Object singletonObject = singletonObjects.get(name);
        // 如果一级缓存中没有，则调用工厂方法创建对象
        if (singletonObject == null) {
            try {
                singletonObject = objectFactory.getObject();
                this.singletonObjects.put(name, singletonObject);
            }catch (Exception e){
                throw e;//  抛出异常
            }

        }
        return singletonObject;
    }
    /**
     * 移除单例对象
     */
    @Override
    public Object remove(String name) {
        //移除单例对象
        Object removeBean = this.singletonObjects.remove(name);
        ///  移除销毁回调
        this.destructionCallbacks.remove(name);
        return removeBean;
    }

    /**
     * 注册销毁回调
     * @param name Bean名称
     * @param callback 回调方法
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        this.destructionCallbacks.put(name, callback);
    }
    /**
     * 获取会话ID
     * @return 会话ID
     */
    @Override
    public String getConversationId() {
        return "singleton";
    }

    /**
     * 执行所有注册的销毁回调
     * 在应用关闭时调用
     */
    public void destroySingletons() {
        //  获取所有注册的销毁回调
        String[] singletonNames = this.destructionCallbacks.keySet().toArray(new String[0]);
            for (String name : singletonNames){
                Runnable callback = this.destructionCallbacks.remove(name);
                if (callback != null){
                    // 执行销毁回调
                    try {
                        //  执行销毁回调
                        callback.run();
                    }
                    catch (Throwable ex) {
                        System.err.println("Exception thrown while executing destruction callback for singleton [" + name + "]: " + ex);
                    }
                }
            }
            //  清空单例对象缓存
        this.singletonObjects.clear();
    }


}
