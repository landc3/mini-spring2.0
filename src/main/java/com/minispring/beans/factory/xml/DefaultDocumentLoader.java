package com.minispring.beans.factory.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * 默认的XML 文档加载器实现
 * 使用DOM4j 的SAXReader解析XML文档
 */
public class DefaultDocumentLoader implements DocumentLoader{

    /**
     * 从输入流中加载XML文档
     * @param inputStream XML文档的输入流
     * @return XML文档
     * @throws DocumentException XML文档加载失败时抛出
     */
    @Override
    public Document loadDocument(InputStream inputStream) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        return saxReader.read(inputStream);
    }
}
