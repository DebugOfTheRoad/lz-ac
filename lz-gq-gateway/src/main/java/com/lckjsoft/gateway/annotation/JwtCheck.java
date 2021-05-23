package com.lckjsoft.gateway.annotation;

import java.lang.annotation.*;

/**
 * @author zhaogaolei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtCheck {

    String value() default "";
}
