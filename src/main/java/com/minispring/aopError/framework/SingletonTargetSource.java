package com.minispring.aopError.framework;
/**
 * 单例目标源实现
 * 每次调用返回相同的目标对象
 */
public class SingletonTargetSource implements TargetSource{
    private final Object target;//目标对象
    /**
     * 创建一个新的单例目标源
     * @param target 目标对象
     */
    public SingletonTargetSource(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Target object must not be null");
        }
        this.target = target;
    }

    /**
     * 返回目标对象的类型
     * @return 目标对象的类型
     */
    @Override
    public Class<?> getTargetClass() {
       return this.target.getClass();
    }

/**
 * 确定目标对象是否为静态的
 * @return 如果目标对象是静态的，则返回true，否则返回false
 */
    @Override
    public boolean isStatic() {
        return true;
    }

    /**
     * 返回目标对象
     * @return 目标对象
     * @throws Exception 如果获取目标对象时发生异常
     */
    @Override
    public Object getTarget() throws Exception {
        return this.target;
    }

    /**
     * 释放目标对象
     * @param target 要释放的目标对象
     * @throws Exception 如果释放目标对象时
     * @throws IllegalStateException 如果目标对象与存储的目标对象不匹配
     */
    @Override
    public void releaseTarget(Object target) throws Exception {
        // 单例不需要释放
    }
    /**
     * 比较此目标源是否与另一目标源相等
     * @param other 另一个目标源
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SingletonTargetSource)) {
            return false;
        }
        SingletonTargetSource otherTargetSource = (SingletonTargetSource) other;
        return this.target.equals(otherTargetSource.target);
    }
    /**
     * 返回此目标源的哈希码
     */
    @Override
    public int hashCode() {
        return this.target.hashCode();
    }
}
