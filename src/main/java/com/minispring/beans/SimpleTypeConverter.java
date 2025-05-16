package com.minispring.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 简单类型转换器
 * 实现基本的类型转换功能
 */
public class SimpleTypeConverter implements TypeConverter {


    /**
     * 类型转换器
     * key: 源类型
     * value: 转换器
     */
    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {

        try {

            if (value == null) {
                return null;// 如果值是null，直接返回null
            }
            if (requiredType.isInstance(value)) {
                return (T) value;// 如果值是目标类型的实例，直接返回值
            }

            Object convertedValue = null;// 转换后的值

            //字符串类型转换器
            if (value instanceof String) {
                String strValue = (String) value;
                convertedValue = convertFromString(strValue, requiredType);

            }
            //数字类型之间的转换
            else if (value instanceof Number) {
                Number numberValue = (Number) value;
                convertedValue = convertNumber(numberValue, requiredType);
            }
            //其他类型转换为字符串类型
            else {
                convertedValue = convertToString(value, requiredType);
            }
            return (T) convertedValue;// 返回转换后的值
        } catch (Exception e) {
            throw new TypeMismatchException(value, requiredType, e);
        }
    }

    /**
     * 其他类型转换为字符串类型
     * @param value
     * @param requiredType String
     * @return
     * @param <T>
     */
    private <T> Object convertToString(Object value, Class<T> requiredType) {
        if (requiredType!=String.class){
            // 如果目标类型不是字符串，抛出类型不匹配异常
            throw new TypeMismatchException("无法将 [" + value + "] 转换为 [" + requiredType.getName() + "]");
        }
        return value.toString();// 返回字符串类型的值
    }

    /**
     * 数字类型转换
     * @param numberValue
     * @param requiredType
     * @return
     * @param <T>
     */
    private <T> Object convertNumber(Number numberValue, Class<T> requiredType) throws TypeMismatchException {
        if(requiredType==Integer.class||requiredType==int.class){
            return numberValue.intValue();// 将数字转换为整数并返回
        }
        if (requiredType==Long.class||requiredType==long.class){
            return numberValue.longValue();// 将数字转换为长整型并返回
        }
        if (requiredType==Double.class||requiredType==double.class){
            return numberValue.doubleValue();// 将数字转换为双精度浮点数并返回
        }
        if (requiredType==Float.class||requiredType==float.class){
            return numberValue.floatValue();// 将数字转换为单精度浮点数并返回
        }
        if (requiredType==Short.class||requiredType==short.class){
            return numberValue.shortValue();// 将数字转换为短整型并返回
        }
        if (requiredType==Byte.class||requiredType==byte.class){
            return numberValue.byteValue();// 将数字转换为字节并返回
        }
        if (requiredType==String.class){
            return numberValue.toString();// 将数字转换为字符串并返回
        }
        throw new TypeMismatchException("不支持的数字类型转换: " + numberValue.getClass().getName() +
                " -> " + requiredType.getName());
    }

    /**
     * 从字符串转换为目标类型
     * @param value
     * @param requiredType
     * @return
     *
     */
    private Object convertFromString(String value, Class<?> requiredType) {
        // 基本类型转换
        if (requiredType == Integer.class || requiredType == int.class) {
            return Integer.parseInt(value);
        }
        if (requiredType == Long.class || requiredType == long.class) {
            return Long.parseLong(value);
        }
        if (requiredType == Double.class || requiredType == double.class) {
            return Double.parseDouble(value);
        }
        if (requiredType == Float.class || requiredType == float.class) {
            return Float.parseFloat(value);
        }
        if (requiredType == Boolean.class || requiredType == boolean.class) {
            String stringValue = value.toLowerCase().trim();
            if ("true".equals(stringValue) ||
                    "yes".equals(stringValue) ||
                    "1".equals(stringValue) ||
                    "on".equals(stringValue) ||
                    "y".equals(stringValue)) {
                return true;
            } else if ("false".equals(stringValue) ||
                    "no".equals(stringValue) ||
                    "0".equals(stringValue) ||
                    "off".equals(stringValue) ||
                    "n".equals(stringValue)) {
                return false;
            }
            throw new IllegalArgumentException("无法将字符串 [" + value + "] 转换为Boolean");
        }
        if (requiredType == Byte.class || requiredType == byte.class) {
            return Byte.parseByte(value);
        }
        if (requiredType == Short.class || requiredType == short.class) {
            return Short.parseShort(value);
        }
        if (requiredType == Character.class || requiredType == char.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("无法将字符串 [" + value + "] 转换为Character，字符串长度必须为1");
            }
            return value.charAt(0);
        }

        // 日期时间类型转换
        if (requiredType == LocalDate.class) {
            return LocalDate.parse(value);
        }
        if (requiredType == LocalTime.class) {
            return LocalTime.parse(value);
        }
        if (requiredType == LocalDateTime.class) {
            try {
                // 尝试使用ISO格式解析
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException e) {
                // 尝试使用常见格式解析
                DateTimeFormatter[] formatters = {
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                };

                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDateTime.parse(value, formatter);
                    } catch (DateTimeParseException ignored) {
                        // 继续尝试下一个格式
                    }
                }

                // 如果是纯日期格式，尝试转换为当天开始时间
                try {
                    return LocalDate.parse(value).atStartOfDay();
                } catch (DateTimeParseException ignored) {
                    // 继续尝试其他格式
                }

                throw new IllegalArgumentException("无法将 [" + value + "] 转换为LocalDateTime，支持的格式：" +
                        "ISO, yyyy-MM-dd HH:mm:ss, yyyy/MM/dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, " +
                        "yyyy.MM.dd HH:mm:ss, yyyy-MM-dd HH:mm, yyyy/MM/dd HH:mm, yyyy-MM-dd");
            }
        }

        // 如果目标类型就是String，直接返回
        if (requiredType == String.class) {
            return value;
        }

        throw new TypeMismatchException("不支持的类型转换: " + value.getClass().getName() +
                " -> " + requiredType.getName());
    }

}