package com.minispring.core.env;

/**
 * 环境接口
 * 表示当前程序的运行环境
 * 提供属性访问和profile（配置文件）功能
 */
public interface Environment {

    /**
     * 获取指定名称的属性值
     * @param key 属性名称
     */
    String getProperty(String key);
    /**
     * 获取指定名称的属性值，如果属性不存在，返回默认值
     * @param key 属性名称
     * @param defaultValue 默认值
     */
    String getProperty(String key, String defaultValue);
    /**
     * 获取指定名称的属性值，并转换为指定类型
     *
     * @param key 属性名称
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 属性值，如果不存在返回null
     */
    <T> T getProperty(String key, Class<T> targetType);
    /**
     * 获取指定名称的属性值，并转换为指定类型的值，如果属性不存在，返回默认值
     * @param key 属性名称
     * @paran targetType 目标类型
     * @param defaultValue 默认值
     * @param <T> 目标类型泛型
     *
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);
    /**
     * 判断指定的属性名称是否激活
     */
    boolean containsProperty(String key);

    /**
     * 获取当前激活的profiles
     * return profiles数组，如果不存在返回null
     */
    String[] getActiveProfiles();
    /**
     * 获取默认的profiles
     * return profiles数组，如果不存在返回null
     */
    String[] getDefaultProfiles();

    /**
     * 判断指定的profiles中是否有任一激活
     *
     * @param profiles profile名称数组
     * @return 如果有任一激活返回true，否则返回false
     */
    boolean acceptsProfiles(String... profiles);

    /**
     * 判断指定的profile是否激活
     *
     * @param profile profile名称
     * @return 如果激活返回true，否则返回false
     */
    boolean acceptsProfiles(String profile);



}
