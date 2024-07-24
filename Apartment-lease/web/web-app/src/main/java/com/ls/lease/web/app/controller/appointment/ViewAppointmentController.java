package com.ls.lease.web.app.controller.appointment;


import com.ls.lease.common.login.LoginUserHolder;
import com.ls.lease.common.result.Result;
import com.ls.lease.model.entity.ViewAppointment;
import com.ls.lease.web.app.service.ViewAppointmentService;
import com.ls.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.ls.lease.web.app.vo.appointment.AppointmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "看房预约信息")
@RestController
@RequestMapping("/app/appointment")
public class ViewAppointmentController {

    @Autowired
    private ViewAppointmentService viewAppointmentService;
    @Operation(summary = "保存或更新看房预约")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ViewAppointment viewAppointment) {
        viewAppointmentService.saveOrUpdate(viewAppointment);
        return Result.ok();
    }

    @Operation(summary = "查询个人预约看房列表")
    @GetMapping("listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        List<AppointmentItemVo> result = viewAppointmentService.listItem(userId);
        return Result.ok(result);
    }

    @GetMapping("getDetailById")
    @Operation(summary = "根据预约ID查询预约详情信息")
    public Result<AppointmentDetailVo> getDetailById(Long id) {
        AppointmentDetailVo result = viewAppointmentService.getDetailById(id);
        return Result.ok(result);
    }

}

