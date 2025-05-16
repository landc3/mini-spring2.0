package com.minispring.beans.factory.config;

/**
 * 占位符解析器
 *用于解析属性值中的占位符，如${...}
 */
public interface PlaceholderResolver {

    /**
     * 解析包含占位符的字符串
     * @param value 包含占位符的字符串
     * @return 解析后的字符串
     */
    String resolvePlaceholders(String value);

    /**
     * 判断是否包含占位符
     */
    boolean containsPlaceholder(String value);
}
