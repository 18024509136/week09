package com.geek;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rpc服务注解
 *
 * @author huangxiaodi
 * @since 2021-05-26 11:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * 接口类名
     *
     * @return
     */
    Class<?> value() default Object.class;
}
