package com.ls.lease.web.app.mapper;

import com.ls.lease.model.entity.LeaseAgreement;
import com.ls.lease.web.app.vo.agreement.AgreementDetailVo;
import com.ls.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.LeaseAgreement
*/
public interface LeaseAgreementMapper extends BaseMapper<LeaseAgreement> {

    //根据租约id查询详细信息
    AgreementDetailVo getDetailLeaseAgreementById(Long id);
}




