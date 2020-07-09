package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @author xxyWi
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {
}
