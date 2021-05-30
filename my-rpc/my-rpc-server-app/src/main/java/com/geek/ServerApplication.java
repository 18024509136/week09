package com.geek;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 服务端app启动类
 *
 * @author huangxiaodi
 * @since 2021-05-26 16:00
 */
public class ServerApplication {

    public static void main(String[] args) {
        // 加载spring上下文，从而启动服务
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext("com.geek");
    }
}
