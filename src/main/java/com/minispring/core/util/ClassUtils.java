package com.minispring.core.util;

/**
 * 类工具，提供类加载器相关的方法
 */
public class ClassUtils {

    /**
     * 获取默认的类加载器
     * 给默认资源加载器DefaultResourceLoader使用
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            // 首先，尝试获取当前线程的上下文类加载器
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法访问线程上下文类加载器，忽略
        }
        if (cl == null) {
            // 其次，使用ClassUtils类的类加载器
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // 最后，使用系统类加载器
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法获取系统类加载器，忽略
                }
            }
        }
        return cl;
    }


    /**
     * 判断给定的类是否存在
     * @param className 类名
     * @param classLoader 类加载器
     * @return 如果存在返回true，否则返回false
     */
    /**
     * （1）initialize = true 的含义
     * 如果 initialize 设置为 true，则在加载类时会执行以下操作：
     * 执行类的静态初始化代码块（如 static {} 块）。
     * 初始化类的静态变量。
     * 2）initialize = false 的含义
     * 如果 initialize 设置为 false，则在加载类时不会执行静态初始化代码块，也不会初始化静态变量。
     *
     * 在这个方法中，initialize 被设置为 false 的原因可以从以下几个方面进行分析：
     * （1）性能优化
     * 如果只需要判断类是否存在，而不需要实际使用该类，那么没有必要执行静态初始化代码块。
     * 静态初始化可能会涉及复杂的逻辑，例如文件读取、资源加载等，这会增加不必要的开销。
     * （3）符合方法的职责
     * 这个方法的职责仅仅是判断类是否存在，而不是实际使用该类。
     * 因此，没有必要执行静态初始化代码块，只需验证类是否可以被加载即可。
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            // 尝试加载指定的类
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
