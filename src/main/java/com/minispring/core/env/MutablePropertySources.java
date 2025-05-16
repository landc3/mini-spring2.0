package com.minispring.core.env;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 可变属性元集合
 * 提供对属性源的增删改操作
 */
public class MutablePropertySources implements PropertySources{

    /**
     * 属性源列表
     * 使用线程安全的CopyOnWriteArrayList
     */
    private final List<PropertySource<?>> propertySources;

    /**
     * 默认构造函数
     */
    public MutablePropertySources() {
        this.propertySources = new CopyOnWriteArrayList<>();
    }

    /**
     * 使用有属性源列表的构造函数
     * @param propertySources 属性源列表
     * @return
     */
    public MutablePropertySources(List<PropertySource<?>> propertySources){
        this.propertySources = new CopyOnWriteArrayList<>(propertySources);
    }

    /**
     * 获取属性元列表，返回副本以避免修改
     * @return
     */
    public List<PropertySource<?>> getPropertySources() {
        return new CopyOnWriteArrayList<>(this.propertySources);
    }

    /**
     * 添加属性源到顶部
     * @return
     */
    public void addFirst(PropertySource<?> propertySource){
        removeIfPresent(propertySource);
        this.propertySources.add(0, propertySource);
    }

    /**
     * 添加属性源到底部
     * @return
     */
    public void addLast(PropertySource<?> propertySource) {
        removeIfPresent(propertySource);
        this.propertySources.add(propertySource);
    }

    /**
     * 如果属性源已存在，则移除
     * @return
     *
     * propertySource -> propertySource.getName().equals(name)
     * 这是一个 Lambda 表达式，用来定义移除条件。
     * 它的含义是：
     *  遍历集合中的每个 source 元素。
     *  调用 source.getName() 获取当前元素的名称。
     *  检查该名称是否与目标属性源 propertySource.getName() 相等。
     */
    private void removeIfPresent(PropertySource<?> propertySource){
        this.propertySources.removeIf(source -> source.getName().equals(propertySource.getName()));
    }

    /**
     * 在指定名称的属性源之前添加属性源
     *
     * @param relativePropertySourceName 相对属性源名称
     * @param propertySource 要添加的属性源
     * @throws IllegalArgumentException 如果相对属性源不存在
     */
    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        removeIfPresent(propertySource);// 如果属性源已存在则移除
        int index = indexOf(relativePropertySourceName);// 获取相对属性源的索引
        if (index == -1) {
            throw new IllegalArgumentException(
                    "属性源 '" + relativePropertySourceName + "' 不存在");
        }
        this.propertySources.add(index, propertySource);
    }
    /**
     * 在指定名称的属性源之后添加属性源
     *
     * @param relativePropertySourceName 相对属性源名称
     * @param propertySource 要添加的属性源
     * @throws IllegalArgumentException 如果相对属性源不存在
     */
    public void addAfter(String relativePropertySourceName,PropertySource<?> propertySource){
        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        removeIfPresent(propertySource);
        int index = indexOf(relativePropertySourceName);
        if (index == -1){
            throw new IllegalArgumentException(
                    "属性源 '" + relativePropertySourceName + "' 不存在");
        }
        this.propertySources.add(index + 1,propertySource);
    }

    /**
     * 替换指定名称的属性源
     * @param name 属性源名称
     * @return
     */
    public void replace(String name,PropertySource<?> propertySource){
        int index = indexOf(name);
        if (index == -1){
            throw new IllegalArgumentException("属性源 '" + name + "' 不存在");
        }
        this.propertySources.set(index,propertySource);
    }

    /**
     * 移除指定名称的属性源
     * @param name 属性源名称
     * @return
     */
    public PropertySource<?> remove(String name) {
        int index = indexOf(name);
        if (index != -1) {// 如果存在则移除
            return this.propertySources.remove(index);
        }
        return null;
    }

    /**
     * 检查相对添加是否合法
     *
     * @param relativePropertySourceName 相对属性源名称
     * @param propertySource 要添加的属性源
     * @throws IllegalArgumentException 如果相对添加不合法
     */
    private void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource){
        if (relativePropertySourceName == null) {
            throw new IllegalArgumentException("相对属性源名称不能为空");
        }
        if (propertySource == null) {
            throw new IllegalArgumentException("属性源不能为空");
        }
        if (relativePropertySourceName.equals(propertySource.getName())){
            throw new IllegalArgumentException("相对属性源与要添加的属性源不能同名");
        }
    }

    /**
     * 获取指定名称的属性源索引
     *
     * @param name 属性源名称
     * @return 索引，如果不存在返回-1
     */
    private int indexOf(String name){
        for (int i = 0; i<this.propertySources.size();i++){
            if (this.propertySources.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }


    /**
     * 获取指定名称的属性源
     * @param name 属性源名称
     * @return
     */
    @Override
    public PropertySource<?> get(String name) {
        for (PropertySource<?> propertySource : this.propertySources){
            if (propertySource.getName().equals(name)){
                return propertySource;
            }
        }
        return null;
    }

    /**
     * 判断指定名称的属性源是否存在
     * @param name 属性源名称
     * @return
     *
     * propertySource -> propertySource.getName().equals(name)
     * 这是一个 Lambda 表达式
     * 它的含义是：
     * 遍历集合中的每个 source 元素。
     * 调用 source.getName() 获取当前元素的名称。
     * 检查该名称是否与目标属性源 propertySource.getName() 相等。
     */
    @Override
    public boolean contains(String name) {
        return this.propertySources.stream()
                .anyMatch(propertySource -> propertySource.getName().equals(name));
    }

    /**
     * 获取属性源迭代器
     * @return
     */
    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySources.iterator();
    }
}
