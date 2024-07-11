package com.ls.lease.web.admin.custom.converter;

import com.ls.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 自定义 Converter 方式
 * 实现Converter接口 import org.springframework.core.convert.converter.Converter;
 */
//在ioc中注册，方便使用
@Component
public class StringToItemType implements Converter<String,ItemType> {

    @Override
    public ItemType convert(String code) {
        //如果新增枚举实例就新增一个if
//        if ("1".equals(code)){
//            return ItemType.APARTMENT;
//        } else if ("2".equals(code)) {
//            return ItemType.ROOM;
//        }

        //用枚举类型的静态方法，获取所有枚举类型的实例
        ItemType[] values = ItemType.values();

        //遍历枚举类型实例，如果枚举类型的实例的code等于参数code，就返回这个枚举实例
        for (ItemType value : values) {
            if (value.getCode().equals(Integer.valueOf(code))){
                return value;
            }
        }
        throw new IllegalArgumentException("code:" + code  +"非法");
    }
}
