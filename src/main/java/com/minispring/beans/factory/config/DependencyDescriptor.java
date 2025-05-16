package com.minispring.beans.factory.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 依赖描述器
 * 用于描述字段或方法参数的依赖注入点
 * 为或许支持@Autowired等注解提供基本支持
 * 推荐：
 * 1
 * 构造器注入 → 强制依赖
 * 2
 * Setter方法注入 → 可选依赖
 * 3
 * 字段注入 → 简单场景
 */
public class DependencyDescriptor {
    private Field field;//字段
    private Parameter methodParameter;//方法参数
    private Method method;//方法
    private int parameterIndex;//方法参数索引
    private Class<?> declaringClass;//声明类
    private boolean required;//是否必须
    private String dependencyName;//依赖名称
    private String parameterName;//参数名称

    /**
     * 创建一个字段依赖描述器
     * @param field 字段
     * param required 是否必须
     * true表示必须，false表示可选
     *
     */
    public DependencyDescriptor(Field field, boolean required) {
        this.field = field;
        this.required = required;
        this.declaringClass = field.getDeclaringClass();
    }
    /**
     * 创建一个方法参数依赖描述器
     *
     * @param required 是否必须
     * true表示必须，false表示可选
     */
    public DependencyDescriptor(Method method,int parameterIndex, boolean required) {
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.methodParameter = method.getParameters()[parameterIndex];
        this.declaringClass = method.getDeclaringClass();
        this.required = required;

    }

    /**
     * 创建一个构造函数参数依赖描述符
     *
     * @param parameter 构造函数参数
     * @param required 是否必须注入
     */
    public DependencyDescriptor(Parameter parameter, boolean required) {
        this.methodParameter = parameter;
        this.declaringClass = parameter.getDeclaringExecutable().getDeclaringClass();
        this.required = required;
    }

    //获取依赖类型
    public Class<?> getDependencyType() {
        if (this.field != null) {
            return this.field.getType();
        }
        if (this.methodParameter != null) {
            return this.methodParameter.getType();
        }
        return null;
    }
    /**
     * 获取依赖名称
     */
    public String getDependencyName() {
        if (this.dependencyName != null) {
            return this.dependencyName;
        }
        if (this.field != null) {
            this.dependencyName = this.field.getName();
        }
        if (this.methodParameter != null) {
            this.dependencyName = this.methodParameter.getName();
        }
        return this.dependencyName;
    }


    /**
     * 设置依赖名称
     *
     * @param dependencyName 依赖名称
     */
    public void setDependencyName(String dependencyName) {
        this.dependencyName = dependencyName;
    }


    /**
     * 获取字段
     *
     * @return 字段
     */
    public Field getField() {
        return this.field;
    }

    /**
     * 获取方法参数
     *
     * @return 方法参数
     */
    public Parameter getMethodParameter() {
        return this.methodParameter;
    }

    /**
     * 获取方法
     *
     * @return 方法
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * 获取参数索引
     *
     * @return 参数索引
     */
    public int getParameterIndex() {
        return this.parameterIndex;
    }

    /**
     * 获取声明类
     *
     * @return 声明类
     */
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    /**
     * 是否必须注入
     *
     * @return 是否必须注入
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * 设置参数名称
     *
     * @param parameterName 参数名称
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }


}
