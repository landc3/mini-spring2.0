package com.minispring.test;

import com.minispring.beans.BeansException;
import com.minispring.beans.factory.ObjectFactory;
import com.minispring.beans.factory.config.PrototypeScope;
import com.minispring.beans.factory.config.SingletonScope;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 作用域测试类
 */
public class ScopeTest {

    /**
     * 测试单例作用域
     * @param
     */
    @Test
    public void testSingletonScope() {
        SingletonScope singletonScope = new SingletonScope();

        //  创建两个单例对象
        Object bean1 =singletonScope.get("singleton", new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws BeansException {
                        return new TestBean();
                    }
                }
        );
        Object bean2 = singletonScope.get("singleton", new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws BeansException {
                        return new TestBean();
                    }
                }
        );
        // 在单例作用域中，两次获取的应该是同一个对象
        assertSame(bean1, bean2, "单例作用域应返回同一个对象实例");

    }
    /**
     * 测试Prototype作用域
     * @param
     */
    @Test
    public void testPrototypeScope() {
        PrototypeScope prototypeScope = new PrototypeScope();
        // 第一次获取Bean
        Object bean1 = prototypeScope.get("testBean", new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws BeansException {
                return new TestBean();
            }
        });

        // 第二次获取Bean
        Object bean2 = prototypeScope.get("testBean", new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws BeansException {
                return new TestBean();
            }
        });

        // 在原型作用域中，两次获取的应该是不同的对象
        assertNotSame(bean1, bean2, "原型作用域应返回不同的对象实例");
    }
    /**
     * 测试销毁回调
     *
     */
    @Test
    public void testDestructionCallback() {
        SingletonScope singletonScope = new SingletonScope();

        TestBean bean1 =(TestBean) singletonScope.get("singleton1", new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws BeansException {
                        return new TestBean();
                    }
                }
        );
        singletonScope.registerDestructionCallback("singleton1", new Runnable() {
                @Override
                public void run() {
                    bean1.destroy();
                }
        });
//        singletonScope.destroySingletons();
        // 执行销毁
        assertFalse(bean1.isDestroyed(), "销毁前Bean不应该是已销毁状态");
        singletonScope.destroySingletons();
        assertTrue(bean1.isDestroyed(), "销毁后Bean应该是已销毁状态");
    }


    /**
     * 测试用的Bean类
     */
    static class TestBean {
        private boolean destroyed = false;

        public void destroy() {
            this.destroyed = true;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
