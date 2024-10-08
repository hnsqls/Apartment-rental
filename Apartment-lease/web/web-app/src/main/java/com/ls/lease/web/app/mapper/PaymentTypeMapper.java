package com.ls.lease.web.app.mapper;

import com.ls.lease.model.entity.PaymentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【payment_type(支付方式表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.PaymentType
*/
public interface PaymentTypeMapper extends BaseMapper<PaymentType> {


    List<PaymentType> selectListByRoomId(Long id);

    //根据房间id查询支付方式名称
    List<PaymentType> listByRoomId(Long id);
}




