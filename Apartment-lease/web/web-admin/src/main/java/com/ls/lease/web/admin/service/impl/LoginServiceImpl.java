package com.ls.lease.web.admin.service.impl;

import com.ls.lease.common.constant.RedisConstant;
import com.ls.lease.web.admin.service.LoginService;
import com.ls.lease.web.admin.vo.login.CaptchaVo;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成验证码图片和uuid
     * 加入redis 缓存 uuid， 验证码得值
     * 返回给前端uuid 和验证码图片
     * @return
     */
    @Override
    public CaptchaVo getCaptcha() {

        //获取验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);

        // 获取验证码得值，转小写，忽略大小写不同
        String code = specCaptcha.text().toLowerCase();

        //生成key
//        String key = "admin:login" + UUID.randomUUID();
        //统一前缀,创建个类，
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();


        //加入redis缓存
        //统一管理
//        redisTemplate.opsForValue().set(key,code,60, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(key,code,RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);



        //图片转为字符传   base64编码
        String picture01 = specCaptcha.toBase64();
        return new CaptchaVo(picture01,key);
    }
}
