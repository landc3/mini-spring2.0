package com.minispring.beans.factory;

/**
 *Bean销毁接口
 * 实现此接口的Bean会在容器销毁时调用其destroy方法
 * 这是Spring生命周期的一个重要拓展点，用于释放资源
 */
public interface DisposableBean {
    /**
     * 在bean被销毁之前调用此方法
     * 可以做一些资源释放工作，如关闭数据库连接，释放文件锁等
     * @throws Exception
     */
    void destroy() throws Exception;
}
