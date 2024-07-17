package com.ls.lease.web.admin.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.common.result.Result;
import com.ls.lease.model.entity.UserInfo;
import com.ls.lease.model.enums.BaseStatus;
import com.ls.lease.web.admin.service.UserInfoService;
import com.ls.lease.web.admin.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户信息管理")
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "分页查询用户信息")
    @GetMapping("page")
    public Result<IPage<UserInfo>> pageUserInfo(@RequestParam long current, @RequestParam long size, UserInfoQueryVo queryVo) {
        Page<UserInfo> userInfoPage = new Page<>(current, size);
        IPage<UserInfo> result = userInfoService.pageUserInfo(userInfoPage,queryVo);

        return Result.ok(result);
    }

    @Operation(summary = "根据用户id更新账号状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BaseStatus status) {
        return Result.ok();
    }
}
