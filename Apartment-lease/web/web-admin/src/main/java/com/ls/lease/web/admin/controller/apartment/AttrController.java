package com.ls.lease.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ls.lease.common.result.Result;
import com.ls.lease.model.entity.AttrKey;
import com.ls.lease.model.entity.AttrValue;
import com.ls.lease.web.admin.service.AttrKeyService;
import com.ls.lease.web.admin.service.AttrValueService;
import com.ls.lease.web.admin.vo.attr.AttrKeyVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间属性管理
 *
 */

@Tag(name = "房间属性管理")
@RestController
@RequestMapping("/admin/attr")
public class AttrController {
    @Autowired
    private AttrKeyService attrKeyService;

    @Autowired
    private AttrValueService attrValueService;

    @Operation(summary = "新增或更新属性名称")
    @PostMapping("/key/saveOrUpdate")
    public Result saveOrUpdateAttrKey(@RequestBody AttrKey attrKey) {
        attrKeyService.saveOrUpdate(attrKey);
        return Result.ok();
    }

    @Operation(summary = "新增或更新属性值")
    @PostMapping("value/saveOrUpdate")
    public Result saveOrUpdateAttrValue(@RequestBody AttrValue attrValue) {
        attrValueService.saveOrUpdate(attrValue);
        return Result.ok();
    }


    @Operation(summary = "查询全部属性名称和属性值列表")
    @GetMapping("list")
    public Result<List<AttrKeyVo>> listAttrInfo() {
        //先查属性名称k， 在查k对应的v     k作为参数----》查询v
        //mp 没有提供多表查询，所以要自定义sql
        List<AttrKeyVo> list= attrKeyService.listAttrInfo();

        return Result.ok(list);
    }

    @Operation(summary = "根据id删除属性名称")
    @DeleteMapping("key/deleteById")
    public Result removeAttrKeyById(@RequestParam Long attrKeyId) {
        //删除属性名称，属性值也没必要存在， 也要删除

        attrKeyService.removeById(attrKeyId);
        LambdaQueryWrapper<AttrValue> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<AttrValue> eq = queryWrapper.eq(AttrValue::getAttrKeyId, attrKeyId);
        attrValueService.remove(eq);
        return Result.ok();
    }

    @Operation(summary = "根据id删除属性值")
    @DeleteMapping("value/deleteById")
    public Result removeAttrValueById(@RequestParam Long id) {
        attrValueService.removeById(id);
        return Result.ok();
    }

}
