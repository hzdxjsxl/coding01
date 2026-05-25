package com.example.blankingmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_order")
@Schema(description = "订单信息表")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单主键ID (雪花算法)")
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long id;

    @TableField("customer_id")
    @Schema(description = "关联的客户ID")
    private Long customerId;

    @TableField("contact_name")
    @Schema(description = "客户姓名 (快照)")
    private String contactName;
    @TableField("company_name")
    @Schema(description = "客户公司 (快照)")
    private String companyName;

    @TableField("phone")
    @Schema(description = "联系方式 (快照)")
    private String phone;
    @TableField("order_name")
    @Schema(description = "订单说明")
    private String orderName;

    @TableField("total_amount")
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @TableField("paid_amount")
    @Schema(description = "已支付金额")
    private BigDecimal paidAmount;

    @TableField("unpaid_amount")
    @Schema(description = "未支付金额")
    private BigDecimal unpaidAmount;

    @TableField("order_time")
    @Schema(description = "下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    @TableField("delivery_time")
    @Schema(description = "交付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deliveryTime;

    @TableField("status")
    @Schema(description = "完成状态 (0:待交付, 2:部分交付, 1:已完成)")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除 (0:未删除, 1:已删除)")
    private Integer isDeleted;
}