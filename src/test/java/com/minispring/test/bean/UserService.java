package com.minispring.test.bean;

/**
 * 用户服务类
 * 用于测试IoC容器
 */
public class UserService {

    private String name;// 用户名
    private UserDao userDao;// 用户数据访问对象

    public void init() {
        System.out.println("执行UserService初始化方法");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String queryUserInfo() {
        return userDao.queryUserName(name);
    }
} 