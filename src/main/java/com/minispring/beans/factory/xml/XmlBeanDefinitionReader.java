package com.minispring.beans.factory.xml;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.support.AbstractBeanDefinitionReader;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.tree.DefaultDocument;

import java.io.IOException;
import java.io.InputStream;

/**
 * XML Bean定义读取器
 * 用于从XML资源中加载Bean定义
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    // XML文档加载器
    private DocumentLoader documentLoader = new DefaultDocumentLoader();
    // XML文档读取器
    private BeanDefinitionDocumentReader beanDefinitionDocumentReader = new DefaultBeanDefinitionDocumentReader();

    /**
     * XML标签和属性常量
     */
    public static final String BEAN_ELEMENT = "bean";// bean标签
    public static final String PROPERTY_ELEMENT = "property";// 属性标签
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String REF_ATTRIBUTE = "ref";// ref标签
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String SCOPE_ATTRIBUTE = "scope";// scope标签 默认为singleton

    /**
     * 构造函数
     * @param registry Bean定义注册表
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 构造函数
     * @param registry Bean定义注册表
     * @param resourceLoader 资源加载器
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    /**
     * 设置文档加载器
     * @param documentLoader 文档加载器
     */
    public void setDocumentLoader(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }

    /**
     * 设置Bean定义文档读取器
     * @param beanDefinitionDocumentReader Bean定义文档读取器
     */
    public void setBeanDefinitionDocumentReader(BeanDefinitionDocumentReader beanDefinitionDocumentReader) {
        this.beanDefinitionDocumentReader = beanDefinitionDocumentReader;
    }

    /**
     * 从资源中加载Bean定义
     * @param resource 资源
     * @throws BeansException Bean定义加载异常
     */
    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try (InputStream inputStream = resource.getInputStream()) {
            doLoadBeanDefinitions(inputStream, resource);
        } catch (IOException | DocumentException e) {
            throw new XmlBeanDefinitionStoreException("解析XML文件失败 [" + resource + "]", e);
        }
    }

    /**
     * 从输入流中加载Bean定义
     * @param inputStream 输入流
     * @param resource 资源（用于错误报告）
     * @throws DocumentException XML解析异常
     */
    protected void doLoadBeanDefinitions(InputStream inputStream, Resource resource) throws DocumentException {
        // 使用DocumentLoader加载XML文档
        Document document = documentLoader.loadDocument(inputStream);

        // 使用BeanDefinitionDocumentReader注册Bean定义
        beanDefinitionDocumentReader.registerBeanDefinitions(document, getRegistry());

        // 记录日志
        System.out.println("从资源 [" + resource + "] 加载了Bean定义");
    }
}
