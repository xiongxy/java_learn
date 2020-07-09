package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @author xxyWi
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {
    String value() default "";
}
