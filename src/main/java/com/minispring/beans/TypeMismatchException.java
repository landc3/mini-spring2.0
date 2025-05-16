package com.minispring.beans;

/**
 * 类型不匹配异常
 * 当类型转换失败时抛出此异常
 */
public class TypeMismatchException extends BeansException {

    //目标类型
    private Class<?> requiredType;
    //需要转换的值
    private Object value;
    /**
     * 构造类型不匹配异常
     *
     * @param value 需要转换的值
     * @param requiredType 目标类型
     */
    public TypeMismatchException(Object value, Class<?> requiredType) {
        super("无法将值 [" + value + "] 转换为类型 [" + requiredType.getName() + "]");
        this.value = value;
        this.requiredType = requiredType;
    }

    /**
     * 构造类型不匹配异常
     *
     * @param value 需要转换的值
     * @param requiredType 目标类型
     * @param cause 原始异常
     */
    public TypeMismatchException(Object value, Class<?> requiredType, Throwable cause) {
        super("类型不匹配: " + value + "不能转换为" + requiredType.getName(), cause);
        this.requiredType = requiredType;
        this.value = value;
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public TypeMismatchException(String message) {
        super(message);
        this.requiredType = null;
        this.value = null;
    }

    public TypeMismatchException(String message, Throwable cause, Class<?> requiredType, Object value) {
        super(message, cause);
        this.requiredType = requiredType;
        this.value = value;
    }

    /**
     * 获取目标类型
     *
     * @return 目标类型
     */
    public Class<?> getRequiredType() {
        return requiredType;
    }

    /**
     * 获取需要转换的值
     *
     * @return 需要转换的值
     */
    public Object getValue() {
        return value;
    }
}