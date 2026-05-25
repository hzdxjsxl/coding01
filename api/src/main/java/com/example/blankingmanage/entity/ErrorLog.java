package com.example.blankingmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@Data
@TableName("sys_error_log")
public class ErrorLog {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long id;

    @Schema(description = "请求的用户id")
    @TableField("request_userid")
    private Long requestUserId;

    @Schema(description = "请求的用户名")
    @TableField("request_username")
    private String requestUserName;

    @Schema(description = "请求的URL地址")
    @TableField("request_uri")
    private String requestUri;

    @Schema(description = "请求方式(GET/POST等)")
    @TableField("request_method")
    private String requestMethod;

    @Schema(description = "前端传来的请求参数(JSON格式)")
    @TableField("request_params")
    private String requestParams;

    @Schema(description = "报错简述(截取前500字符)")
    @TableField("error_brief")
    private String errorBrief;

    @Schema(description = "完整报错堆栈信息")
    @TableField("error_stack")
    private String errorStack;

    @Schema(description = "报错发生时间")
    @TableField("create_time")
    private Date createTime;
}
