package com.minispring.core.env;

import java.util.Objects;

/**
 * 属性源抽象类
 * 表示一个键值对形式的属性源
 *
 * @param <T> 属性源的底层来源类型
 */
public abstract class PropertySource<T> {
    /**
     * 属性源名称
     */
    private final String name;
    /**
     * 属性源的底层来源
     */
    protected final T source;

    /**
     * 构造函数
     *
     * @param name 属性源名称
     * @param source 属性源的底层来源
     */
    public PropertySource(String name, T source) {
        this.name = name;
        this.source = source;
    }
    /**
     * 获取属性源名称
     *
     * @return 属性源名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取属性源底层来源
     *
     * @return 属性源底层来源
     */
    public T getSource() {
        return this.source;
    }

    /**
     * 判断属性源是否包含指定名称的属性
     * @param name 属性名称
     * return 如果包含返回true，否则返回false
     */
    public boolean containsProperty(String name) {
        return getProperty(name) != null;
    }
    /**
     * 获取指定名称的属性值
     * @param name 属性名称
     * @return 属性值
     */
    public abstract Object getProperty(String name);

    /**
     * 重写equals方法
     * 根据属性源名称判断两个属性源是否相等
     * @param other 另一个属性源
     *
     * 三重判断
     *  equals 方法首先检查 other 是否是 PropertySource 类型的实例。
     * 如果不是，则直接返回 false，因为不同类型的对象不可能相等。
     * 如果是，则将其强制转换为 PropertySource 类型，并进一步比较属性值。
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) { // 判断是否为同一对象
            return true;
        }
        if (!(other instanceof PropertySource)){// 判断是否是属性源
            return false;
        }
        PropertySource<?> otherSource = (PropertySource<?>) other;// 将other转换为属性源类型
        return this.name.equals(otherSource.name);
    }

    /**
     * 重写hashCode方法
     * 根据属性源名称计算哈希值
     * @return 哈希值
     */
    @Override
    public int hashCode() {
       return this.name.hashCode();
    }
    /**
     * 重写toString方法
     *
     * @return 属性源的名称
     */
    @Override
    public String toString() {
//      return this.getClass().getSimpleName() + " [" + this.name + "]";
        return getClass().getSimpleName() + " {name='" + this.name + "'}";
    }


}
