package com.minispring.beans.factory.xml;

import com.minispring.beans.BeansException;

/**
 * XML Bean定义存储异常
 * 用于处理XML解析过程中的异常
 */
public class XmlBeanDefinitionStoreException extends BeansException{

    /**
     * 构造函数，用于创建一个XML Bean定义存储异常
     *
     * @param message 异常信息
     */
    public XmlBeanDefinitionStoreException(String message) {
        super(message);
    }

    /**
     * 构造函数，用于创建一个XML Bean定义存储异常
     *
     * @param message 异常信息
     * @param cause   异常 cause
     */
    public XmlBeanDefinitionStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
