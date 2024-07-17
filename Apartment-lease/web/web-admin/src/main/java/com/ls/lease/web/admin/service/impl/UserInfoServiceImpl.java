package com.ls.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.model.entity.UserInfo;
import com.ls.lease.web.admin.service.UserInfoService;
import com.ls.lease.web.admin.mapper.UserInfoMapper;
import com.ls.lease.web.admin.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public IPage<UserInfo> pageUserInfo(Page<UserInfo> userInfoPage, UserInfoQueryVo queryVo) {
        return userInfoMapper.pageUserInfo(userInfoPage,queryVo);
    }
}




