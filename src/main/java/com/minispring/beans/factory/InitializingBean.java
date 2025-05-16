package com.minispring.beans.factory;

/**
 * Bean初始化接口
 * 实现此接口的Bean会在初始化时调用afterPropertiesSet方法
 * 这是Spring生命周期的一个重要拓展点
 */
public interface InitializingBean {
    /**
     * 在Bean的所有属性设置完之后调用
     * 可以在这里自定义初始化逻辑
     * throws Exception 初始化过程中会抛出的异常
     */
    void afterPropertiesSet() throws Exception;

}
