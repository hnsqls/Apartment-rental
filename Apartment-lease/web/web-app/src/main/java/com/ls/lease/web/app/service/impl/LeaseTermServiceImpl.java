package com.ls.lease.web.app.service.impl;

import com.ls.lease.model.entity.LeaseTerm;
import com.ls.lease.web.app.mapper.LeaseTermMapper;
import com.ls.lease.web.app.service.LeaseTermService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_term(租期)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseTermServiceImpl extends ServiceImpl<LeaseTermMapper, LeaseTerm>
        implements LeaseTermService {

    @Autowired
     private LeaseTermMapper leaseTermMapper;

    /**
     * 根据房间id查询可选租期
     * @param id
     * @return
     */
    @Override
    public List<LeaseTerm> listByRoomId(Long id) {
        return leaseTermMapper.listByRoomId(id);
    }
}




