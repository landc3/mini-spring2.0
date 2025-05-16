package com.minispring.core.io;

import com.minispring.core.util.ClassUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 默认资源加载器
 *支持以下资源类型：
 *  1. URL资源：http://, https://, file://, ftp://等
 *  2. ClassPath资源：classpath:
 *  3. 文件系统资源：默认
 */
public class DefaultResourceLoader implements ResourceLoader{
    private ClassLoader classLoader;

    public DefaultResourceLoader(ClassLoader classLoader) {
        // 如果外部传入了ClassLoader，则使用外部传入的ClassLoader
        this.classLoader = classLoader;
    }

    public DefaultResourceLoader() {
        // 如果外部没有传入ClassLoader，则使用默认的ClassLoader
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    // 根据资源路径获取资源
    /**
     * （1）加载 classpath 资源
     * 不需要 try...catch，因为 ClassPathResource 的构造方法不会抛出受检异常。
     * 即使发生异常（如 IllegalArgumentException），也属于运行时异常，通常不需要显式捕获。
     * （2）尝试作为 URL 资源加载
     * 需要 try...catch，因为 new URL(String spec) 方法会抛出受检异常 MalformedURLException。
     * 我们希望通过捕获异常来实现降级处理（将路径视为文件系统资源）。
     * （3）作为文件系统资源加载
     * 不需要额外的 try...catch，因为 FileSystemResource 的构造方法不会抛出受检异常。
     */
    @Override
    public Resource getResource(String resourceLocation) {
        if (resourceLocation == null || resourceLocation.isEmpty()) {
            throw new IllegalArgumentException("资源路径不能为空");
        }

        // 判断是否是classpath资源
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            // 加载classpath资源
            return new ClassPathResource(resourceLocation.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        }

        try {
            // 尝试作为URL资源加载
            URL url = new URL(resourceLocation);
            return new UrlResource(url);
        } catch (MalformedURLException ex) {
            // 作为文件系统资源加载
            return new FileSystemResource(resourceLocation);
        }
    }

    //获取类加载器
    @Override
    public ClassLoader getClassLoader() {
        //如果外部没有传入ClassLoader，则使用默认的ClassLoader
        return (this.classLoader==null?ClassUtils.getDefaultClassLoader():this.classLoader);
    }

    //设置类加载器

    /**
     * （1）为什么需要 setClassLoader 方法？
     * 支持动态配置：允许用户在运行时指定类加载器。
     * 适应不同环境：满足复杂运行环境下的需求。
     * 提高扩展性：让框架更灵活，适应更多场景。
     * （2）不可变性与可变性的权衡
     * 默认情况下使用不可变的类加载器，保证稳定性。
     * 提供 setClassLoader 方法，增加灵活性。
     * （3）适用场景
     * 如果类加载器不需要动态调整，则可以忽略 setClassLoader 方法。
     * 如果需要在运行时动态切换类加载器，则可以使用 setClassLoader 方法。
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
