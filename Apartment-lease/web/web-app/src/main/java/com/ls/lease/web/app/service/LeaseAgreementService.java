package com.ls.lease.web.app.service;

import com.ls.lease.model.entity.LeaseAgreement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.lease.web.app.vo.agreement.AgreementDetailVo;

/**
* @author liubo
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface LeaseAgreementService extends IService<LeaseAgreement> {
    /**
     * 根据租约id获取详细租约信息
     * @param id
     * @return
     */
    AgreementDetailVo getDetailLeaseAgreementById(Long id);
}
