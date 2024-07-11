package com.ls.lease.web.admin.custom.converter;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.ls.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class StringToBaseEnumsConverter implements ConverterFactory<String, BaseEnum>{
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new Converter<String, T>() {
            @Override
            public T convert(String code) {
                for (T enumConstant : targetType.getEnumConstants()) {
                    if (enumConstant.getCode().equals(Integer.valueOf(code))){
                        return enumConstant;
                    }
                }
                throw  new IllegalArgumentException("code：" + code + "非法");
            }
        };
    }
}
