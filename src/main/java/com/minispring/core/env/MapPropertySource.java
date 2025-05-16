package com.minispring.core.env;

import java.util.Map;

/**
 * 基于Map的属性源实现
 * 从Map中获取属性值
 */
public class MapPropertySource extends PropertySource<Map<String, Object>>{

    /**
     * 构造函数
     * 调用父类构造函数：
     * super(name, source) 调用了父类（通常是 PropertySource）的构造函数，
     * 将名称和底层数据传递给父类进行初始化。
     * 泛型参数：
     * <String, Object> 是 Java 泛型的一部分，用于指定 Map 中键和值的类型。
     * String 表示键：
     * 键的类型是 String，表示配置项的名称（如 app.name、app.version）。
     * 键通常用于唯一标识一个配置项。
     * Object 表示值：
     * 值的类型是 Object，表示配置项的具体值。
     * 值可以是任何类型的对象（如字符串、数字、布尔值等），因此使用 Object 作为泛型参数以支持多种类型。
     */
    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }
    /**
     * 获取指定名称的属性值
     *
     * @param name 属性名称
     * @return 属性值，如果不存在返回null
     */
    @Override
    public Object getProperty(String name) {
        return this.source.get(name);
    }
    /**
     * 判断属性源是否包含指定名称的属性
     *
     * @param name 属性名称
     * @return 如果包含返回true，否则返回false
     */
    @Override
    public boolean containsProperty(String name) {
        return this.source.containsKey(name);
    }
}
