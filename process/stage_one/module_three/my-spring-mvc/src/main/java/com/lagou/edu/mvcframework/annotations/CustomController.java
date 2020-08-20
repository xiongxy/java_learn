package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author xxyWi
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomController {
    String value() default "";
}
