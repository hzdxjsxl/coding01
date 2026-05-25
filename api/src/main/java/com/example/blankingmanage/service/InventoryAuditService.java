package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blankingmanage.entity.InventoryFlow;

public interface InventoryAuditService {
    /**
     * 执行流水审核
     * @param flowId 流水ID
     * @param action 动作: 1-通过, 2-驳回
     * @param auditUserId 库管人员ID
     * @param remark 备注/驳回原因
     */
    void executeAudit(Long flowId, Integer action, Long auditUserId, String remark);
}
