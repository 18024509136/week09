package com.geek;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDubbo(scanBasePackages = {"com.geek.capacity"})
@MapperScan("com.geek.mapper")
@SpringBootApplication
@EnableTransactionManagement
public class DubboHmilyBankaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboHmilyBankaServiceApplication.class, args);
    }

}
