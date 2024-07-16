package com.ls.lease.web.admin.service;

import com.ls.lease.model.entity.ApartmentInfo;
import com.ls.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【apartment_info(公寓信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface ApartmentInfoService extends IService<ApartmentInfo> {


    void saveOrUpdateapart(ApartmentSubmitVo apartmentSubmitVo);

    IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo);

    ApartmentDetailVo getDetailById(Long id);

    void deleteApartmentByID(Long id);
}
