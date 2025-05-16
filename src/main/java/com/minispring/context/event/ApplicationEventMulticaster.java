package com.minispring.context.event;

import com.minispring.context.ApplicationEvent;

/**
 * 应用事件多播器接口
 * 负责将事件广播给所有注册的监听器
 */
public interface  ApplicationEventMulticaster {
    /**
     * 添加事件监听器（注册）
     */
    void addApplicationListener(ApplicationListener<?> listener);
    /**
     * 移除事件监听器
     * @param listener 要移除的监听器
     */
    void removeApplicationListener(ApplicationListener<?> listener);
    /**
     * 删除事件监听器
     */
    void removeAllListeners();
    /**
     * 将事件多播给所有匹配的监听器
     *
     * @param event 要多播的事件
     */
    void multicastEvent(ApplicationEvent event);

}
