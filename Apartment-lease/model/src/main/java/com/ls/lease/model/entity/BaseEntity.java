package com.ls.lease.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    @JsonIgnore
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间")
    @JsonIgnore
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    private Date updateTime;

    @JsonIgnore
//    @TableLogic 逻辑删除，默认1删除，0不删除。
    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    private Byte isDeleted;

}