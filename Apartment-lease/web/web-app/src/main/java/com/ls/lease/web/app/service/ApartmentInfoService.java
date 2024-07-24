package com.ls.lease.web.app.service;

import com.ls.lease.model.entity.ApartmentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.lease.web.app.vo.apartment.ApartmentItemVo;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service
 * @createDate 2023-07-26 11:12:39
 */
public interface ApartmentInfoService extends IService<ApartmentInfo> {
    /**
     * 根据公寓id查询公寓详细信息
     * @param id
     * @return
     */
    ApartmentItemVo selectApartmentItemVoById(Long id);
}
