package com.minispring.core.env;

import java.util.Map;

/**
 * 可配置的环境接口
 * 拓展Environment接口，添加一些可配置的属性
 */
public interface ConfigurableEnvironment extends Environment{

    /**
     * 设置活跃的profiles
     * @param profiles 活跃的profiles数组
     */
    void setActiveProfiles(String... profiles);
    /**
     * 添加活跃的profiles
     *
     */
    void addActiveProfile(String profile);
    /**
     * 设置默认的profiles
     */
    void setDefaultProfiles(String... profiles);

    /**
     * 获取系统属性
     * Map
     * String
     * Object
     * @return 系统属性
     */
    Map<String, Object> getSystemProperties();
/**
     * 获取系统环境变量
     * Map
     * String
     * Object
     * @return 系统环境变量
     */
    Map<String, Object> getSystemEnvironment();
    /**
     * 合并另一个Environment的配置
     * @param parent 父Environment
     */
    void merge(ConfigurableEnvironment parent);
    /**
     * 获取属性源集合
     *
     * @return 属性源集合
     */
    MutablePropertySources getPropertySources();

}
