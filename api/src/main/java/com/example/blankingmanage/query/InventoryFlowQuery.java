package com.example.blankingmanage.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.blankingmanage.common.DTO.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class InventoryFlowQuery extends BasePageDTO {
    //流水状态: 0-待确认, 1-已完成, 2-已驳回
    private Integer status;
    //变动类型: 1-入库, 2-出库
    private Integer changeType;
    //物品id
    private Long materialId;
    //快照: 物品名称
    private String materialName;
    //快照: 物品类型
    private String materialType;
    //快照: 发起人姓名
    private String createUserName;
}
