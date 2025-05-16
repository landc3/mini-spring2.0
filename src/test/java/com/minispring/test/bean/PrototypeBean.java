package com.minispring.test.bean;
/**
 * 原型Bean类
 * 用于测试原型作用域
 */
public class PrototypeBean {
    // 属性 Bean的名称
    private String name;
    //创建时间
    private long createTime;
    public PrototypeBean() {
        // 创建时间
        this.createTime = System.currentTimeMillis();
        System.out.println("PrototypeBean构造函数，创建时间：" + createTime);
    }

    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PrototypeBean{" +
                "name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }

}
