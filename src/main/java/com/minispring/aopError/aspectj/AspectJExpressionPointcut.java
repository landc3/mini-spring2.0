package com.minispring.aopError.aspectj;

import com.minispring.aopError.ClassFilter;
import com.minispring.aopError.MethodMatcher;
import com.minispring.aopError.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * AspectJ表达式切点
 * 使用AspectJ的表达式语言定义切点
 */
public class AspectJExpressionPointcut  implements Pointcut, ClassFilter, MethodMatcher {
    //AspectJ表达式支持的切点原语
    private static final HashSet<PointcutPrimitive> SUPPORTED_PRIMITIVES =new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);//执行
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);//参数
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);//引用
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);//this
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);//target
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);//within
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);//atAnnotation
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);//atWithin
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);//atArgs
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);//atTarget
    }
    //切点表达式
    private String expression;
    //切点表达式对象
    private PointcutExpression pointcutExpression;
    //类加载器，用于解析表达式
    private final ClassLoader pointcutClassLoader;

    /**
     * 创建一个新的AspectJExpressionPointcut实例
     */
    public AspectJExpressionPointcut() {
       this(null,  null);
    }

    /**
     * 创建一个新的AspectJExpressionPointcut
     * @param expression 切点表达式
     */
    public AspectJExpressionPointcut(String expression) {
        this(expression, null);
    }

    /**
     * 创建一个新的AspectJExpressionPointcut
     * @param expression 切点表达式
     * @param pointcutClassLoader 类加载器
     */
    public AspectJExpressionPointcut(String expression, ClassLoader pointcutClassLoader) {
        this.expression = expression;
        this.pointcutClassLoader = (pointcutClassLoader != null ? pointcutClassLoader :
                AspectJExpressionPointcut.class.getClassLoader());// 使用当前类的类加载器
        if (expression != null) {
            buildPointcutExpression();///创建切点表达式对象
        }
    }

    public String getExpression() {
        return expression;
    }

    /**
     * 设置切点表达式
     * @param expression 切点表达式
     */
    public void setExpression(String expression) {
        this.expression = expression;
        buildPointcutExpression();
    }
    /**
     * 构建切点表达式
     */
    private void buildPointcutExpression() {
        if (this.expression == null) {
            throw new IllegalStateException("Expression must not be null");
        }

        PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES, this.pointcutClassLoader);
        this.pointcutExpression = parser.parsePointcutExpression(this.expression);
    }

    /**
     * 匹配方法
     * @param method 方法
     * @param targetClass 目标类
     * @return 是否匹配
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        checkReadyToMatch();// 检查是否已准备好匹配
        ShadowMatch shadowMatch = this.pointcutExpression.matchesAdviceExecution(method);//匹配方法执行
        return shadowMatch.alwaysMatches();
    }

    @Override
    public boolean isRuntime() {
        checkReadyToMatch();
        return this.pointcutExpression.mayNeedDynamicTest();
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        checkReadyToMatch();
        ShadowMatch shadowMatch = this.pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();

    }

    @Override
    public boolean matches(Class<?> clazz) {
        checkReadyToMatch();
        return this.pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
    /**
     * 检查是否已准备好匹配
     */
    private void checkReadyToMatch() {
        if (this.pointcutExpression == null) {
            throw new IllegalStateException("Must set expression before attempting to match");
        }
    }

}

