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
@TableName("sys_inventory_flow")
@Schema(description = "订单信息表")
public class InventoryFlow {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单主键ID (雪花算法)")
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long id;

    @TableField("material_id")
    @Schema(description = "关联的物品ID")
    private Long materialId;
    @TableField("change_type")
    @Schema(description = "变动类型: 1-入库, 2-出库")
    private Integer changeType; // 1-入库, 2-出库
    @TableField("change_type")
    @Schema(description = "变动数量")
    private BigDecimal changeNum;
    @TableField("before_num")
    @Schema(description = "变动前总数")
    private BigDecimal beforeNum;
    @TableField("after_num")
    @Schema(description = "变动后总数 (预计算值)")
    private BigDecimal afterNum;
    @TableField("relation_order_no")
    @Schema(description = "关联订单号/业务单号")
    private String relationOrderNo;
    @TableField("create_user_id")
    @Schema(description = "发起人ID (业务员)")
    private Long createUserId;
    @TableField("create_time")
    @Schema(description = "发起时间")
    private LocalDateTime createTime;

    // 状态机与审核
    @TableField("status")
    @Schema(description = "状态: 0-待确认, 1-已完成, 2-已驳回")
    private Integer status; // 0-待确认, 1-已完成, 2-已驳回
    @TableField("audit_user_id")
    @Schema(description = "审核人ID (库管)")
    private Long auditUserId;
    @TableField("audit_time")
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    // 工作流暗门
    @TableField("process_instance_id")
    @Schema(description = "工作流实例ID(预留)")
    private String processInstanceId;
    @TableField("remark")
    @Schema(description = "备注/驳回原因")
    private String remark;
    @TableField("material_name")
    @Schema(description = "快照: 物品名称")
    private String materialName;
    @TableField("material_type")
    @Schema(description = "快照: 物品类型")
    private String materialType;
    @TableField("create_user_name")
    @Schema(description = "快照: 发起人姓名")
    private String createUserName;

}