package com.ls.lease.web.app.controller.login;


import com.ls.lease.common.result.Result;
import com.ls.lease.web.app.service.LoginService;
import com.ls.lease.web.app.service.SmsService;
import com.ls.lease.web.app.vo.user.LoginVo;
import com.ls.lease.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理")
@RestController
@RequestMapping("/app/")
public class LoginController {

    @Autowired
    private  LoginService loginService;

    @GetMapping("login/getCode")
    @Operation(summary = "获取短信验证码")
    public Result getCode(@RequestParam String phone) {
        loginService.getCode(phone);
        return Result.ok();
    }

    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String result =  loginService.login(loginVo);
        return Result.ok(result);
    }

    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    public Result<UserInfoVo> info() {
        UserInfoVo result =  loginService.getUserById();
        return Result.ok(result);
    }
}

