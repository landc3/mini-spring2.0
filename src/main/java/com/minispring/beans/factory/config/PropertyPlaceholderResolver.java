package com.minispring.beans.factory.config;

import java.util.Properties;

/**
 * 属性占位符解析器
 * 用于解析${...}格式的占位符
 */
public class PropertyPlaceholderResolver implements PlaceholderResolver{

    // 占位符前缀
    private static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    // 占位符后缀
    private static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    //占位符与默认值的分隔符
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private final String placeholderPrefix; // 自定义占位符前缀
    private final String placeholderSuffix;// 自定义占位符后缀
    private final String valueSeparator;// 自定义占位符与默认值的分隔符
    private final Properties properties;// 属性集合（属性源）

    /**
     * 使用默认的占位符前缀、后缀、分隔符构造一个属性占位符解析器
     * @param properties 属性集合（属性源）
     */
    public PropertyPlaceholderResolver(Properties properties) {
        this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, properties);
    }
    /**
     * 使用自定义的占位符前缀、后缀、分隔符构造一个属性占位符解析器
     * @param placeholderPrefix 占位符前缀
     * @param placeholderSuffix 占位符后缀
     * @param valueSeparator 占位符与默认值的分隔符
     * @param properties 属性集合（属性源）
     */
    public PropertyPlaceholderResolver(String placeholderPrefix, String placeholderSuffix, String valueSeparator, Properties properties) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        this.valueSeparator = valueSeparator;
        this.properties = properties;
    }


    /**
     * 解析占位符
     * @param value 待解析的占位符
     * @return 解析后的值
     */
    /**
     * 解析占位符
     *
     * @param value 待解析的属性值
     * @return 解析后的属性值
     */
    @Override
    public String resolvePlaceholders (String value) {
        if (value == null || value.isEmpty() || !containsPlaceholder(value)) {
            return value;
        }

        StringBuilder result = new StringBuilder(value);
        int startIndex = result.indexOf(placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = result.indexOf(placeholderSuffix, startIndex + placeholderPrefix.length());
            if (endIndex != -1) {
                // 提取占位符内容
                String placeholder = result.substring(startIndex + placeholderPrefix.length(), endIndex);
                String defaultValue = null;

                // 检查是否有默认值
                int separatorIndex = placeholder.indexOf(valueSeparator);
                if (separatorIndex != -1) {
                    defaultValue = placeholder.substring(separatorIndex + valueSeparator.length());
                    placeholder = placeholder.substring(0, separatorIndex);
                }

                // 查找属性值
                String propVal = properties.getProperty(placeholder);
                if (propVal == null && defaultValue != null) {
                    propVal = defaultValue;
                }

                if (propVal != null) {
                    // 替换占位符
                    result.replace(startIndex, endIndex + placeholderSuffix.length(), propVal);
                    // 更新下一个查找位置
                    startIndex = result.indexOf(placeholderPrefix, startIndex + propVal.length());
                } else {
                    // 没有找到属性值，保留原占位符
                    startIndex = result.indexOf(placeholderPrefix, endIndex + placeholderSuffix.length());
                }
            } else {
                // 没有找到结束标记，退出循环
                break;
            }
        }

        return result.toString();
    }

    /**
     * 判断是否包含占位符
     * @param value 待判断的字符串
     * @return 是否包含占位符
     */
    @Override
    public boolean containsPlaceholder(String value) {
        // 如果待判断的字符串为空，或者不包含占位符的前缀和后缀，则返回false
        return value!=null && value.contains(placeholderPrefix) && value.contains(placeholderSuffix);
    }
}
