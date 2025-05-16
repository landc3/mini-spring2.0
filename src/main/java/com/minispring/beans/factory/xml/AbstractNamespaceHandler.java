package com.minispring.beans.factory.xml;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;

import org.dom4j.Element;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽象命名空间处理器
 * 提供命名空间处理器的基本实现
 * 模板方法模式的实现，提供元素解析器和属性装饰器的注册机制
 * 强制子类必须实现init()完成注册（未在代码中显式声明，但符合Spring约定）
 */
public abstract class AbstractNamespaceHandler implements NamespaceHandler{
    /**
     * 元素解析器映射表
     */
    private final Map<String, ElementParser> elementParsers= new HashMap<>();

    /**
     * 属性装饰器映射表
     */
    private final Map<String, AttributeDecorator> attributeDecorators= new HashMap<>();

    /**
     * 注册元素解析器
     * @param elementName 元素名
     * @param elementParser 元素解析器
     */
    public void registerElementParser(String elementName, ElementParser elementParser){
        elementParsers.put(elementName, elementParser);
    }
    /**
     * 注册属性装饰器
     * @param attributeName 属性名
     * @param attributeDecorator 属性装饰器
     */
    public void registerAttributeDecorator(String attributeName, AttributeDecorator attributeDecorator){
        attributeDecorators.put(attributeName, attributeDecorator);
    }



    /**
     * 解析元素
     * @param element 要解析的元素
     * @param registry Bean定义注册表
     * @throws BeansException 如果解析过程中发生错误
     */
    @Override
    public void parse(Element element, BeanDefinitionRegistry registry) throws BeansException {
            String localName = element.getName();// 获取元素的本地名称（不包含命名空间前缀）
        ElementParser elementParser = elementParsers.get(localName);// 获取元素解析器
        if (elementParser != null){
            // 解析元素
            elementParser.parse(element, registry);
        }else {
            throw new XmlBeanDefinitionStoreException("未知的元素 [" + localName + "] 在命名空间 [" + element.getNamespaceURI() + "]");
        }

    }

    /**
     * 解析属性
     * @param element 要解析的元素
     * @param attributeName 要解析的属性名
     * @param registry Bean定义注册表
     * @throws BeansException 如果解析过程中发生错误
     */
    @Override
    public void decorate(Element element, String attributeName, BeanDefinitionRegistry registry) throws BeansException {
        AttributeDecorator decorator = attributeDecorators.get(attributeName);
        if (decorator != null) {
            decorator.decorate(element, attributeName, registry);
        } else {
            throw new XmlBeanDefinitionStoreException("未知的属性 [" + attributeName + "] 在命名空间 [" + element.getNamespaceURI() + "]");
        }


    }


    public interface ElementParser{
        /**
         * 元素解析器
         *  @param element 要解析元素
         * @param registry Bean定义注册表
         *  throws BeansException 如果解析过程中发生错误
         */
        void parse(Element element, BeanDefinitionRegistry registry) throws BeansException;
    }
    public interface AttributeDecorator{
        /**
         * 属性装饰器
         * @param element 要解析的元素
         * @param attributeName 要解析的属性名
         * @param registry Bean定义注册表
         * @throws BeansException 如果解析过程中发生错误
         */
        void decorate(Element element, String attributeName, BeanDefinitionRegistry registry) throws BeansException;
    }


}
