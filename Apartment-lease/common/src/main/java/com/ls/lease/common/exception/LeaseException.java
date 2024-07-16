package com.ls.lease.common.exception;

import com.ls.lease.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义异常处理 扩展RuntimeException
 */
@Data
public class LeaseException extends RuntimeException{
    private  Integer code;

    public LeaseException(Integer code,String message){
        super(message);//父类字段不能直接修改，但是可以调用方法
        this.code = code;
    }

    public LeaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code=resultCodeEnum.getCode();
    }
}
