package com.ls.lease.common.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.context.annotation.Configuration;

@Configuration

@MapperScan("com.ls.lease.web.admin.mapper")
public class MybatisPlusConfiguration {
}
