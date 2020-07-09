package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @author xxyWi
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}
