package com.ls.lease.web.app.service.impl;

import com.ls.lease.model.entity.ApartmentInfo;
import com.ls.lease.model.entity.ViewAppointment;
import com.ls.lease.web.app.mapper.ApartmentInfoMapper;
import com.ls.lease.web.app.mapper.ViewAppointmentMapper;
import com.ls.lease.web.app.service.ApartmentInfoService;
import com.ls.lease.web.app.service.ViewAppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.app.vo.apartment.ApartmentItemVo;
import com.ls.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.ls.lease.web.app.vo.appointment.AppointmentItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Autowired
    private  ViewAppointmentMapper viewAppointmentMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    /**
     * 根据用户id查询预约信息列表
     * @param userId
     * @return
     */
    @Override
    public List<AppointmentItemVo> listItem(Long userId) {

        return viewAppointmentMapper.listItem(userId);
    }

    /**
     * 根据预约id查询预约详细信息
     * @param id
     * @return
     */
    @Override
    public AppointmentDetailVo getDetailById(Long id) {
        ViewAppointment viewAppointment = viewAppointmentMapper.selectById(id);
        //根据公寓id查询详细公寓信息
        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(viewAppointment.getApartmentId());

        AppointmentDetailVo appointmentDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(viewAppointment,appointmentDetailVo);
        appointmentDetailVo.setApartmentItemVo(apartmentItemVo);
        return appointmentDetailVo;
    }
}




