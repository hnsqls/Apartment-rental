package com.ls.lease.common.mp;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus 的自动填充功能
 * 1. 确定填充时机，比如实体对象的创建时间，插入时间。
 *          前端一般不会给值，需要后端获得对象，手动set，但是每次新增或者更改操作都要这样操作，太麻烦
 *          mp有自动填充的功能。
 *          实现mp的自动填充过程
 *              1.在需要填充的字段上加上@TableFile(fill = Feild.xxx) 确定填充时机
 *                   @TableField(value = "create_time",fill = FieldFill.INSERT)
 *              2. 实现MetaObjectHandler，如下代码，别忘了加上组件。
 *
 *
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

    }
}
