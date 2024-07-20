package com.ls.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ls.lease.common.constant.RedisConstant;
import com.ls.lease.common.exception.LeaseException;
import com.ls.lease.common.result.ResultCodeEnum;
import com.ls.lease.common.utils.JwtUtil;
import com.ls.lease.model.entity.SystemUser;
import com.ls.lease.model.enums.BaseStatus;
import com.ls.lease.web.admin.mapper.SystemUserMapper;
import com.ls.lease.web.admin.service.LoginService;
import com.ls.lease.web.admin.vo.login.CaptchaVo;
import com.ls.lease.web.admin.vo.login.LoginVo;
import com.ls.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SystemUserMapper systemUserMapper;

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

    /**
     * 校验登录
     * - 前端发送`username`、`password`、`captchaKey`、`captchaCode`请求登录。
     * - 判断`captchaCode`是否为空，若为空，则直接响应`验证码为空`；若不为空进行下一步判断。
     * - 根据`captchaKey`从Redis中查询之前保存的`code`，若查询出来的`code`为空，则直接响应`验证码已过期`；若不为空进行下一步判断。
     * - 比较`captchaCode`和`code`，若不相同，则直接响应`验证码不正确`；若相同则进行下一步判断。
     * - 根据`username`查询数据库，若查询结果为空，则直接响应`账号不存在`；若不为空则进行下一步判断。
     * - 查看用户状态，判断是否被禁用，若禁用，则直接响应`账号被禁`；若未被禁用，则进行下一步判断。
     * - 比对`password`和数据库中查询的密码，若不一致，则直接响应`账号或密码错误`，若一致则进行入最后一步。
     * - 创建JWT，并响应给浏览器。
     * @param loginVo
     * @return
     */
    @Override
    public String login(LoginVo loginVo) {
        //判断`captchaCode`是否为空，若为空，则直接响应`验证码为空`；若不为空进行下一步判断。
        if(loginVo.getCaptchaCode() == null){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }
        //`captchaKey`从Redis中查询之前保存的`code`，若查询出来的`code`为空，则直接响应`验证码已过期`；若不为空进行下一步判断。
        String code = redisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if (code ==null){
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }
        //比较`captchaCode`和`code`，若不相同，则直接响应`验证码不正确`；若相同则进行下一步判断。
        if (!code.equals(loginVo.getCaptchaCode().toLowerCase())){
            throw  new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }
        //根据`username`查询数据库，若查询结果为空，则直接响应`账号不存在`；若不为空则进行下一步判断。

        //由于我们之前设置mybatisplus通用方法不查password，但是这里我们需要使用password，那就自定义sql，查password字段
//        LambdaQueryWrapper<SystemUser> systemUserQueryWrapper = new LambdaQueryWrapper<>();
//        systemUserQueryWrapper.eq(SystemUser::getUsername,loginVo.getUsername());
//        SystemUser systemUser = systemUserMapper.selectOne(systemUserQueryWrapper);
         SystemUser systemUser= systemUserMapper.selectByUsername(loginVo.getUsername());

        if (systemUser == null){
            throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //查看用户状态，判断是否被禁用，若禁用，则直接响应`账号被禁`；若未被禁用，则进行下一步判断。
        if (systemUser.getStatus()== BaseStatus.DISABLE){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);

        }
        //比对`password`和数据库中查询的密码，若不一致，则直接响应`账号或密码错误`，若一致则进行入最后一步。
        if (!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword()))){
            throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }


        return JwtUtil.createToken(systemUser.getId(),systemUser.getUsername());
    }

    /**
     * 根据用户id获得用户信息
     * @param userid
     * @return
     */
    @Override
    public SystemUserInfoVo getLoginUserByID(Long userid) {
        SystemUser systemUser = systemUserMapper.selectById(userid);

        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());
        systemUserInfoVo.setName(systemUser.getUsername());
        return systemUserInfoVo;
    }
}
