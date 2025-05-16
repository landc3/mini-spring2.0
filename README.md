项目描述：
基于Java语言从零开始实现Spring框架核心功能，通过对Spring源码的深入分析和理解，手写实现包含IoC容器、AOP、事务等核心功能的简化版Spring框架。

学习路线：
![image](https://github.com/user-attachments/assets/e89e2a08-5883-48e8-b4f4-fb64696ae673)

项目结构：
```
src/main/java/com/minispring/
├── beans
│   ├── factory
│   │   ├── config
│   │   │   ├── Scope相关接口和实现
│   │   │   └── ...
│   │   ├── support
│   │   └── xml
│   ├── BeansException.java
│   ├── PropertyValue.java
│   ├── PropertyValues.java
│   └── ...
├── context
│   ├── event
│   ├── support
│   ├── ApplicationContext.java
│   └── ...
├── core
│   ├── io
│   ├── convert
│   │   ├── converter
│   │   ├── support
│   │   └── ...
│   └── ...
├── aop
│   ├── framework
│   ├── aspectj
│   ├── Advisor.java
│   ├── PointcutAdvisor.java
│   └── ...
├── web
│   ├── context
│   │   ├── request
│   │   │   ├── RequestScope.java
│   │   │   ├── SessionScope.java
│   │   │   └── ...
│   │   └── ...
│   └── ...
└── util
    └── ...
```

核心功能：
Mini-Spring实现了以下Spring核心功能：
1IoC容器：依赖注入和控制反转 ✅
2AOP：面向切面编程 ✅
3Bean生命周期管理：实例化、初始化、销毁 ✅
4应用上下文：配置和环境管理 ✅
5资源加载：类路径和文件系统资源 ✅
6事件监听机制：发布订阅模式 ✅
7类型转换：基本类型和自定义类型转换 ✅
8Bean作用域：单例、原型及Web作用域 ✅
9循环依赖支持✅
