package com.minispring.core.io;

/**
 * 资源加载器接口
 * 定义了获取资源的统一方法
 */
public interface ResourceLoader {

    /** Classpath URL前缀 */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /** 文件系统URL前缀 */
    String FILE_URL_PREFIX = "file:";

    /** HTTP URL前缀 */
    String HTTP_URL_PREFIX = "http:";

    /** HTTPS URL前缀 */
    String HTTPS_URL_PREFIX = "https:";
    /**
     * 获取资源
     * resourceLocation：资源位置，可以是文件路径、URL、类路径等
     */
    Resource getResource(String resourceLocation);
    /**
     * 获取类加载器
     * 返回当前资源的类加载器
     * 什么是类加载器？
     * 类加载器是Java应用程序的运行时环境，用于加载和执行类和接口。
     */
    ClassLoader getClassLoader();
}
