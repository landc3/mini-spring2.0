package com.minispring.beans.factory.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.InputStream;
/**
 * XML文档加载器接口
 * 用于将XML输入流解析为Document对象
 */
public interface DocumentLoader {
    /**
     * 从输入流中加载XML文档
     * @param inputStream 输入流
     * @return Document对象
     * @throws DocumentException 如果加载XML文档时发生错误
     */
    Document loadDocument(InputStream inputStream) throws DocumentException;
}
