package com.ls.lease.web.app.service;

import com.ls.lease.model.entity.ViewAppointment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.ls.lease.web.app.vo.appointment.AppointmentItemVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface ViewAppointmentService extends IService<ViewAppointment> {
    /**
     * 根据用户id查询预约信息
     * @param userId
     * @return
     */
    List<AppointmentItemVo> listItem(Long userId);

    /**
     * 根据id查看详细信息
     * @param id
     * @return
     */
    AppointmentDetailVo getDetailById(Long id);
}
