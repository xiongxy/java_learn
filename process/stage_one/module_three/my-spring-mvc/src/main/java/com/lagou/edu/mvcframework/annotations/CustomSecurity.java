package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author xxyWindows@hotmail.com
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomSecurity {
    String[] roles() default "";
}
