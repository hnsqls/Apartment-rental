package com.ls.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ls.lease.common.constant.RedisConstant;
import com.ls.lease.common.exception.LeaseException;
import com.ls.lease.common.login.LoginUser;
import com.ls.lease.common.login.LoginUserHolder;
import com.ls.lease.common.result.ResultCodeEnum;
import com.ls.lease.common.sms.AliyunSMSConfiguration;
import com.ls.lease.common.utils.CodeUtil;
import com.ls.lease.common.utils.JwtUtil;
import com.ls.lease.model.entity.UserInfo;
import com.ls.lease.model.enums.BaseStatus;
import com.ls.lease.web.app.mapper.UserInfoMapper;
import com.ls.lease.web.app.service.LoginService;
import com.ls.lease.web.app.service.SmsService;
import com.ls.lease.web.app.service.UserInfoService;
import com.ls.lease.web.app.vo.user.LoginVo;
import com.ls.lease.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.rmi.dgc.Lease;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserInfoMapper userInfoMapper;



    /**
     * 创建验证码，并通过sms发送
     * @param phone
     */
    @Override
    public void getCode(String phone) {
        //使用生成随机数的工具类生成验证码
        String code = CodeUtil.getRandomCode(6);
        String key = RedisConstant.APP_LOGIN_PREFIX+phone;

        //限制频繁发送，在发送之前看看，是不是一分钟之内发过验证码了
        Boolean aBoolean = redisTemplate.hasKey(key);
        if (aBoolean){
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC-ttl< RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC){
                throw  new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }
        //使用sms服务发送目标手机号，验证码
        smsService.sendCode(phone,code);

        //将手机号和验证码加入到redis，并设置过期时间

        redisTemplate.opsForValue().set(key,code,RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);


    }

    /***
     *
     * @param loginVo
     * @return
     */
    @Override
    public String login(LoginVo loginVo) {
        //手机号为空
        if (loginVo.getPhone() == null){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }
        //验证码为空
        if (loginVo.getCode() ==null){
            throw  new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }
        // 验证码超市
        String key = RedisConstant.APP_LOGIN_PREFIX + loginVo.getPhone();
        String code = redisTemplate.opsForValue().get(key);
        if (code ==null){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }
        //验证码错误
        if (!code.equals(loginVo.getCode())){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }

        //查看数据库有没有该用户，没有就先注册，如果有账户就查询是不是被禁用
        LambdaQueryWrapper<UserInfo> userInfoQueryWrapper = new LambdaQueryWrapper<>();
        userInfoQueryWrapper.eq(UserInfo::getPhone,loginVo.getPhone());
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
        if (userInfo ==null){
         //注册
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户"+loginVo.getPhone().substring(7));
            userInfoMapper.insert(userInfo);

        }else {
            //判断是否被禁用
            if (userInfo.getStatus() == BaseStatus.DISABLE){
                throw  new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
            }
        }
        //登录成功返回jwt 。

        return JwtUtil.createToken(userInfo.getId(),userInfo.getPhone());
    }

    /**
     * 获取用户基本信息
     */
    @Override
    public UserInfoVo getUserById() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        UserInfo userInfo = userInfoMapper.selectById(loginUser.getUserId());
        return  new UserInfoVo(userInfo.getNickname(),userInfo.getAvatarUrl());
    }

}
