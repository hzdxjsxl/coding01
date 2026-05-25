package com.example.blankingmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_operation_log")
@Schema(name = "SysOperationLog", description = "系统操作溯源日志表")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("operator_id")
    @Schema(description = "操作人ID")
    private Long operatorId;

    @TableField("operator_name")
    @Schema(description = "操作人姓名(防联表冗余)")
    private String operatorName;

    @TableField("module")
    @Schema(description = "操作模块 (如: 库存管理)")
    private String module;

    @TableField("action_type")
    @Schema(description = "执行操作 (如: 逻辑下料)")
    private String actionType;

    @TableField("ip_address")
    @Schema(description = "操作物理IP(追踪电脑溯源)")
    private String ipAddress;

    @TableField("user_agent")
    @Schema(description = "操作系统与浏览器(追踪电脑溯源)")
    private String userAgent;

    @TableField("request_url")
    @Schema(description = "请求接口路径")
    private String requestUrl;

    @TableField("request_params")
    @Schema(description = "请求具体参数(留底防抵赖)")
    private String requestParams;

    @TableField("status")
    @Schema(description = "执行结果: 1成功, 0报错")
    private Integer status;

    @TableField("error_msg")
    @Schema(description = "报错原因(如有)")
    private String errorMsg;

    @TableField("create_time")
    @Schema(description = "操作精准时间")
    private Date createTime;
}