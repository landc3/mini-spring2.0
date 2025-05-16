package com.minispring.beans.factory.xml;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;
import org.dom4j.Element;
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
 * Context 命名空间处理器
 * 用于处理 context 命名空间下的元素和属性
 */
public class ContextNamespaceHandler extends AbstractNamespaceHandler{
    /**
     * 初始化命名空间处理器
     */
    @Override
    public void init() {
        //注册元素解析器
        registerElementParser("property-placeholder",new PropertyPlaceholderElementParser());
        registerElementParser("component-scan", new ComponentScanElementParser());

        //注册属性装饰器
        registerAttributeDecorator("default-lazy-init", new DefaultLazyInitAttributeDecorator());
    }

    /**
     * 属性占位符元素解析器
     */
    private static class PropertyPlaceholderElementParser implements ElementParser {

        @Override
        public void parse(Element element, BeanDefinitionRegistry registry) throws BeansException {
            String location = element.attributeValue("location");
            if (location!=null&&!location.isEmpty()){
                System.out.println("解析 property-placeholder 元素，加载属性文件：" + location);
                // 实际实现中，这里应该加载属性文件并创建 PropertyPlaceholderConfigurer Bean
            }
        }
    }

    /**
     * 组件扫描元素解析器
     */
    private static class ComponentScanElementParser implements ElementParser {

        /**
         * 元素解析器
         *  @param element 要解析元素
         * @param registry Bean定义注册表
         *  throws BeansException 如果解析过程中发生错误
         */
        @Override
        public void parse(Element element, BeanDefinitionRegistry registry) throws BeansException {
            String basePackage = element.attributeValue("base-package");
            if (basePackage != null && !basePackage.isEmpty()) {
                System.out.println("解析 component-scan 元素，扫描包：" + basePackage);
                // 实际实现中，这里应该扫描指定包下的组件并注册为 Bean
            }
        }
    }


    /**
     * 默认延迟初始化属性装饰器
     */
    private static class DefaultLazyInitAttributeDecorator implements AttributeDecorator {

        /**
         * 属性装饰器
         * @param element 要解析的元素
         * @param attributeName 要解析的属性名
         * @param registry Bean定义注册表
         * @throws BeansException 如果解析过程中发生错误
         */
        @Override
        public void decorate(Element element, String attributeName, BeanDefinitionRegistry registry) throws BeansException {
            String value = element.attributeValue(attributeName);
            if ("true".equals(value)) {
                System.out.println("设置默认延迟初始化为 true");
                // 实际实现中，这里应该设置所有 Bean 的默认延迟初始化属性
            }
        }
    }
}
