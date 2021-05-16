package com.lckjsoft.security.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 23:01
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD,ElementType.TYPE})
public @interface IgnoreUserToken {
}
