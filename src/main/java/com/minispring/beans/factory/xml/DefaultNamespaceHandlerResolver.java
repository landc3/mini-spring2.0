package com.minispring.beans.factory.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * DefaultNamespaceHandlerResolver 负责解析命名空间 URI 并找到对应的命名空间处理器（如 ContextNamespaceHandler）。
 * ContextNamespaceHandler 负责具体解析该命名空间下的自定义标签，并将解析结果注册到 Spring 容器中。
 * 调用顺序
 * Spring 启动时：
 * Spring 容器会加载所有的 XML 配置文件，并解析其中的命名空间。
 * 对于每个命名空间 URI，Spring 会调用 DefaultNamespaceHandlerResolver.resolve(namespaceUri) 来查找对应的命名空间处理器。
 * 实例化命名空间处理器：
 * 如果找到对应的处理器类名，则通过反射实例化并调用其 init() 方法进行初始化。
 * 初始化完成后，处理器会被缓存以供后续使用。
 * 解析自定义标签：
 * 当 Spring 解析到某个命名空间下的自定义标签时，会调用对应命名空间处理器的解析器（如 PropertyPlaceholderElementParser 或 ComponentScanElementParser）来处理该标签。
 */
/**
 * 默认的命名空间处理器解析器实现
 * 从配置文件中加载命名空间URI到处理器类的映射
 */
public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver{

    /**
     * 默认的处理器映射文件路径
     */
    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";

    /**
     * 命名空间URI到处理器类名的映射
     */
    private final Map<String, String> handlerMappings = new HashMap<>();

    /**
     * 已解析的处理器缓存
     */
    private final Map<String, NamespaceHandler> handlerCache = new HashMap<>();

    /**
     * 使用默认的处理器映射文件路径创建解析器
     */
    public DefaultNamespaceHandlerResolver() {
        this(DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    /**
     * 使用指定的处理器映射文件路径创建解析器
     *
     * @param handlerMappingsLocation 处理器映射文件路径
     */
    public DefaultNamespaceHandlerResolver(String handlerMappingsLocation) {
        loadHandlerMappings(handlerMappingsLocation);
    }

    /**
     * 从配置文件加载处理器映射
     *
     * @param handlerMappingsLocation 处理器映射文件路径
     */
    private void loadHandlerMappings(String handlerMappingsLocation) {
        try {
            Properties mappings = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream(handlerMappingsLocation);
            if (is != null) {
                try {
                    mappings.load(is);
                } finally {
                    is.close();
                }

                for (Map.Entry<Object, Object> entry : mappings.entrySet()) {
                    String namespaceUri = (String) entry.getKey();
                    String handlerClassName = (String) entry.getValue();
                    handlerMappings.put(namespaceUri, handlerClassName);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("无法加载命名空间处理器映射: " + handlerMappingsLocation, ex);
        }
    }

    /**
     * 解析命名空间处理器
     *
     * @param namespaceUri 命名空间URI
     * @return 命名空间处理器，如果不存在则返回null
     */
    @Override
    public NamespaceHandler resolve(String namespaceUri) {
        // 先从缓存中查找
        NamespaceHandler handler = handlerCache.get(namespaceUri);
        if (handler != null) {
            return handler;
        }

        // 查找处理器类名
        String handlerClassName = handlerMappings.get(namespaceUri);
        if (handlerClassName == null) {
            return null;
        }

        try {
            // 加载处理器类
            Class<?> handlerClass = Class.forName(handlerClassName);
            if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                throw new IllegalStateException("类 [" + handlerClassName + "] 不是 NamespaceHandler 的实现");
            }

            // 实例化处理器
            handler = (NamespaceHandler) handlerClass.newInstance();

            // 初始化处理器
            handler.init();

            // 缓存处理器
            handlerCache.put(namespaceUri, handler);

            return handler;
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("找不到命名空间处理器类: " + handlerClassName, ex);
        } catch (InstantiationException ex) {
            throw new IllegalStateException("无法实例化命名空间处理器类: " + handlerClassName, ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("无法访问命名空间处理器类: " + handlerClassName, ex);
        }
    }
}
