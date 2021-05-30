package com.geek;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableDubbo(scanBasePackages = {"com.geek"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DubboHmilyTradeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboHmilyTradeCenterApplication.class, args);
    }

}
