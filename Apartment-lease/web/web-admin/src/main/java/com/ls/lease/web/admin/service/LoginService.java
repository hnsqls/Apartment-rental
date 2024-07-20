package com.ls.lease.web.admin.service;

//import com.ls.lease.web.admin.vo.login.CaptchaVo;
//import com.ls.lease.web.admin.vo.login.LoginVo;
//import com.ls.lease.web.admin.vo.system.user.SystemUserInfoVo;

import com.ls.lease.web.admin.vo.login.CaptchaVo;
import com.ls.lease.web.admin.vo.login.LoginVo;
import com.ls.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getLoginUserByID(Long userid);
}
