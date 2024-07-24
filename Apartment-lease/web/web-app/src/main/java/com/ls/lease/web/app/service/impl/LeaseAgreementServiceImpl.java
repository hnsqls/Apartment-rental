package com.ls.lease.web.app.service.impl;

import com.ls.lease.model.entity.LeaseAgreement;
import com.ls.lease.web.app.mapper.LeaseAgreementMapper;
import com.ls.lease.web.app.service.LeaseAgreementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.app.vo.agreement.AgreementDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;
    /**
     * 根据租约id获取详细信息
     * @param id
     * @return
     */
    @Override
    public AgreementDetailVo getDetailLeaseAgreementById(Long id) {
        return leaseAgreementMapper.getDetailLeaseAgreementById(id);
    }
}




