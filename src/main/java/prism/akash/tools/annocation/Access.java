package prism.akash.tools.annocation;

import prism.akash.tools.annocation.checked.AccessType;

import java.lang.annotation.*;

/**
 * 指定方法执行类型
 * TODO 主要用于Schema层方法的鉴权 [ACCESS]
 *
 * @author HaoNan Yan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {

    /**
     * 指定方法的执行类型枚举集合
     *
     * @return
     */
    AccessType[] value();

    /**
     * 当前方法名称
     * @return
     */
    String note() default  "";
}
