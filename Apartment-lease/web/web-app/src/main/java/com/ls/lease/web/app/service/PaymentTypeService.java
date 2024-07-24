package com.ls.lease.web.app.service;

import com.ls.lease.model.entity.PaymentType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liubo
* @description 针对表【payment_type(支付方式表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface PaymentTypeService extends IService<PaymentType> {
    /**
     * 根据房间id查询付款方式
     * @param id
     * @return
     */
    List<PaymentType> listByRoomId(Long id);
}
