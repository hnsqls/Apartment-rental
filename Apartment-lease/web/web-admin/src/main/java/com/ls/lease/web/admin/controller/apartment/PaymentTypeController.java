package com.ls.lease.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ls.lease.common.result.Result;
import com.ls.lease.model.entity.PaymentType;
import com.ls.lease.web.admin.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "支付方式管理")
@RequestMapping("/admin/payment")
@RestController
public class PaymentTypeController {

    @Autowired
    private PaymentTypeService paymentTypeService;
    @Operation(summary = "查询全部支付方式列表")
    @GetMapping("/list")
    public Result<List<PaymentType>> listPaymentType() {
        //mp中有逻辑删除功能，在配置文件中配置后就不用做下面的操作
//        LambdaQueryWrapper<PaymentType> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(PaymentType::getIsDeleted,0);

        List<PaymentType> list = paymentTypeService.list();
        return Result.ok(list);
    }

    @Operation(summary = "保存或更新支付方式")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdatePaymentType(@RequestBody PaymentType paymentType) {
        paymentTypeService.saveOrUpdate(paymentType);
        return Result.ok();
    }

    @Operation(summary = "根据ID删除支付方式")
    @DeleteMapping("deleteById")
    public Result deletePaymentById(@RequestParam Long id) {
        paymentTypeService.removeById(id);
        return Result.ok();
    }

}















