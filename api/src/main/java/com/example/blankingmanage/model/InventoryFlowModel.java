package com.example.blankingmanage.model;

import lombok.Data;

@Data
public class InventoryFlowModel {
    private Long flowId;
    private Integer action; // 1-通过, 2-驳回
    private String remark;
}
