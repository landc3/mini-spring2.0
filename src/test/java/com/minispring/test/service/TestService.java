package com.minispring.test.service;

import com.minispring.test.bean.TestBean;

/**
 * 测试服务类
 * 设计要点：
 * 依赖注入测试
 * 业务方法测试
 * toString() 方法用于调试
 */
public class TestService {
    private TestBean testBean;
    private String message;

    public TestService(TestBean testBean, String message) {
        this.testBean = testBean;
        this.message = message;
    }
    public TestService() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TestBean getTestBean() {
        return testBean;
    }

    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }

    /**
     * 获取服务信息
     * @return
     */
    public String getServiceInfo() {
        return message + " - " + testBean.toString();
    }

    public String toString() {
        return "TestService{" +
                "testBean=" + testBean +
                ", message='" + message + '\'' +
                '}';
    }
}
