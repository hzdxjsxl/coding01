package com.example.blankingmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_customer")
@Schema(description = "客户信息表")
public class Customer {
    @Schema(description = "主键ID (雪花算法)")
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long id;

    @Schema(description = "公司名称")
    @TableField("company_name")
    private String companyName;

    @Schema(description = "联系人")
    @TableField("contact_name")
    private String contactName;

    @Schema(description = "联系电话")
    @TableField("phone")
    private String phone;

    @Schema(description = "发货地址")
    @TableField("address")
    private String address;

    // 建议交给 MySQL 的 DEFAULT CURRENT_TIMESTAMP 处理，代码里无需管
    @Schema(description = "创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除 (0:未删除, 1:已删除)")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
