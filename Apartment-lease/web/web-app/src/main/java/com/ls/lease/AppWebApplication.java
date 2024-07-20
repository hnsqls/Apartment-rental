package com.ls.lease;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ls.lease.web.app.mapper")
public class AppWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class,args);
    }
}
