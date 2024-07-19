package com.ls.lease.web.admin.custom.config;

import com.ls.lease.web.admin.custom.converter.StringToBaseEnumsConverter;
import com.ls.lease.web.admin.custom.converter.StringToItemType;
import com.ls.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    //添加自定义Converter
//    @Autowired
//    private StringToItemType stringToItemType; //由于添加了自定义转换工厂，可以不用

    //添加 ConverterFactory
    @Autowired
    private StringToBaseEnumsConverter stringToBaseEnumsConverter;

    //添加拦截器，校验token
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;
    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(stringToItemType);
        registry.addConverterFactory(stringToBaseEnumsConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login/**");
    }
}
