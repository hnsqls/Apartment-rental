package com.ls.lease.common.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ls.lease.web.*.mapper")
public class MybatisPlusConfiguration {
}
