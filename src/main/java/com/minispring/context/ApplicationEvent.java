package com.minispring.context;

import java.util.EventObject;

/**
 * 应用实践基类
 * 所有事件都应继承此类
 */
public class ApplicationEvent extends EventObject {
    //事件发生时间
    private final long timestamp;

    /**
     * 构造函数
     * @param source 事件源
     */
    public ApplicationEvent(Object source) {
        super(source);// 调用父类构造函数
        // 获取当前时间
        this.timestamp = System.currentTimeMillis();
    }
    /**
     * 获取事件发生时间
     * return 时间戳（毫秒）
     */
    public long getTimestamp() {
        return timestamp;
    }
}
