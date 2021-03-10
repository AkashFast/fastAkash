package prism.akash.tools.annocation;

import java.lang.annotation.*;

/**
 * Schema逻辑类标识注解
 * TODO 主要用于Schema层的初始化 「项目启动时会进行匹配」
 *
 * @author HaoNan Yan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Schema {

    /**
     * schema的唯一标识
     * @return
     */
    String code() default "";

    /**
     * schema的名称
     * @return
     */
    String name() default "";

    /**
     * 是否对当前schema进行初始化，默认为true
     * @return
     */
    boolean init() default true;

}
