package com.example.blankingmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_material")
@Schema(name = "Material", description = "物品基础信息表")
public class Material {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("material_name")
    @Schema(description = "物品名称")
    private String materialName;

    @TableField("material_type")
    @Schema(description = "物品类型（原材料/半成品等）")
    private String materialType;

    @TableField("stock_num")
    @Schema(description = "当前总库存数量")
    private BigDecimal stockNum;

    @TableField("unit")
    @Schema(description = "计量单位")
    private String unit;

    @TableField("description")
    @Schema(description = "物品描述")
    private String description;

    @TableField("purpose")
    @Schema(description = "物品用途")
    private String purpose;

    @TableField("address")
    @Schema(description = "所在地址/库位")
    private String address;

    @TableField("location_image")
    @Schema(description = "位置图片URL")
    private String locationImage;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

    @TableField("is_deleted")
    @Schema(description = "逻辑删除标志")
    @TableLogic
    private Integer isDeleted;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}