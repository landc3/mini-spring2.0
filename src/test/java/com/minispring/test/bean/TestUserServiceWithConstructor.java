package com.minispring.test.bean;

/**
 * 测试用户服务类（构造函数诸如）
 */
public class TestUserServiceWithConstructor {
    private final TestUserDao userDao;

    /**
     * 构造函数注入
     */
    public TestUserServiceWithConstructor() {
        this.userDao = null;
    }
    /**
     * 构造函数注入
     */
    public TestUserServiceWithConstructor(TestUserDao userDao) {
        this.userDao = userDao;
    }
    /**
     * 获取用户信息
     */
    public TestUserDao getUserDao() {
        return userDao;
    }


}
