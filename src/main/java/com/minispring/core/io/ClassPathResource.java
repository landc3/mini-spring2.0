package com.minispring.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 类路径资源加载器
 */
public class ClassPathResource implements Resource{
    private final String path;//路径
    private final  ClassLoader classLoader;//类加载器,用于加载资源文件

    /**
     * 构造方法
     * @param path 路径
     * 没有类加载器参数，这种情况下，使用当前线程的类加载器
     */
    public ClassPathResource(String path) {
        this(path, null);
    }

    public String getPath() {
        return path;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 构造方法
     * @param path 路径
     * @param classLoader 类加载器
     * 有类加载器参数，这种情况下，使用传入的类加载器
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        if (path == null){
            throw new IllegalArgumentException("Path must not be null");
        }
        this.path = path.startsWith("/")? path.substring(1) : path;
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
    }



    @Override
    public boolean exists() {
        return classLoader.getResource(path)!=null;
    }

    /**
     * 判断资源是否可读
     * @return true表示可读，false表示不可读
     * 这里默认返回true，因为类路径下的资源都是可读的
     */
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * 获取资源输入流
     * @return
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream resourceAsStream = classLoader.getResourceAsStream(path);
        if (resourceAsStream == null){
            throw new FileNotFoundException("类路径资源 [" + path + "] 不存在");
        }
        return resourceAsStream;
    }

    /**
     * 获取资源描述信息
     * @return
     */
    @Override
    public String getDescription() {
        return "类路径资源 [" + path + "]";
    }

    /**
     * 获取默认的类加载器
     * @return
     * 忽略获取类加载器失败的情况：
     * 类加载器的获取失败并不一定意味着程序无法继续运行。例如：
     * 如果只是加载某些非关键资源（如配置文件），即使类加载器获取失败，也可以使用其他方式加载。
     * 忽略异常可以让调用者自行决定如何处理这种情况，而不是强制终止程序。
     */

    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            // 获取当前线程的类加载器
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法获取线程上下文类加载器时忽略
        }
        if (cl == null) {
            // 获取当前类的类加载器
            cl = ClassPathResource.class.getClassLoader();
            if (cl == null) {
                try {
                    // 获取系统类加载器
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法获取系统类加载器时忽略
                }
            }
        }
        return cl;
    }
}
