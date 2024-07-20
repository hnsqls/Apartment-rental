package com.ls.lease.web.admin.controller.login;


import com.ls.lease.common.login.LoginUser;
import com.ls.lease.common.login.LoginUserHolder;
import com.ls.lease.common.result.Result;
import com.ls.lease.common.utils.JwtUtil;
import com.ls.lease.web.admin.service.LoginService;
import com.ls.lease.web.admin.vo.login.CaptchaVo;
import com.ls.lease.web.admin.vo.login.LoginVo;
import com.ls.lease.web.admin.vo.system.user.SystemUserInfoVo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理系统登录管理")
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private LoginService service;
    @Operation(summary = "获取图形验证码")
    @GetMapping("login/captcha")
    public Result<CaptchaVo> getCaptcha() {
        CaptchaVo result = service.getCaptcha();
        return Result.ok(result);
    }

    @Operation(summary = "登录")
    @PostMapping("login")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String result = service.login(loginVo);
        return Result.ok(result);
    }

    @Operation(summary = "获取登陆用户个人信息")
    @GetMapping("info")
    public Result<SystemUserInfoVo> info(@RequestHeader("access-token") String token) {
        //下面两行可以拿到用户id，但是这样就解析了两遍token，拦截器解析一次，这里又解析一次。
//        Claims claims = JwtUtil.parseToken(token);
//        Long userId = claims.get("userId", Long.class);

        LoginUser loginUser = LoginUserHolder.getLoginUser();

        System.out.println(loginUser.getUserId());
        System.out.println(loginUser.getUsername());
        Long userId = loginUser.getUserId();

        SystemUserInfoVo result = service.getLoginUserByID(userId);

        return Result.ok(result);
    }
}