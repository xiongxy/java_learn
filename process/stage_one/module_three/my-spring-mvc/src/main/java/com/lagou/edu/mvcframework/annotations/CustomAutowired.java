package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author xxyWi
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAutowired {
    String value() default "";
}
