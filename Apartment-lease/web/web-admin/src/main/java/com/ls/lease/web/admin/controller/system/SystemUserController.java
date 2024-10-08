package com.ls.lease.web.admin.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.common.result.Result;
import com.ls.lease.model.entity.SystemUser;
import com.ls.lease.model.enums.BaseStatus;
import com.ls.lease.web.admin.service.SystemUserService;
import com.ls.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.ls.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.codec.digest.DigestUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "后台用户信息管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @Operation(summary = "根据条件分页查询后台用户列表")
    @GetMapping("page")
    public Result<IPage<SystemUserItemVo>> page(@RequestParam long current, @RequestParam long size, SystemUserQueryVo queryVo) {
        Page<SystemUser> page = new Page<>(current, size);
        IPage<SystemUserItemVo> result =  systemUserService.selectSysUserpage(page,queryVo);
        return Result.ok(result);
    }

    @Operation(summary = "根据ID查询后台用户信息")
    @GetMapping("getById")
    public Result<SystemUserItemVo> getById(@RequestParam Long id) {
        SystemUserItemVo result = systemUserService.getSysUSerById(id);
        return Result.ok(result);
    }

    @Operation(summary = "保存或更新后台用户信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemUser systemUser) {

        if(systemUser.getPassword() != null){
            String md5password = DigestUtils.md5Hex(systemUser.getPassword()); //如果密码为空进行md5处理会异常
            systemUser.setPassword(md5password);
        }
        systemUserService.saveOrUpdate(systemUser);
        return Result.ok();
    }

    @Operation(summary = "判断后台用户名是否可用")
    @GetMapping("isUserNameAvailable")
    public Result<Boolean> isUsernameExists(@RequestParam String username) {
        LambdaQueryWrapper<SystemUser> systemUserQueryWrapper = new LambdaQueryWrapper<>();
        systemUserQueryWrapper.eq(SystemUser::getUsername,username);
        long count = systemUserService.count(systemUserQueryWrapper);

        return Result.ok(count ==0);
    }

    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除后台用户信息")
    public Result removeById(@RequestParam Long id) {
        systemUserService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "根据ID修改后台用户状态")
    @PostMapping("updateStatusByUserId")
    public Result updateStatusByUserId(@RequestParam Long id, @RequestParam BaseStatus status) {

        LambdaUpdateWrapper<SystemUser> systemUserUpdateWrapper = new LambdaUpdateWrapper<>();

        systemUserUpdateWrapper.eq(SystemUser::getId,id)
                        .set(SystemUser::getStatus,status);
        systemUserService.update(systemUserUpdateWrapper);
        return Result.ok();
    }
}
