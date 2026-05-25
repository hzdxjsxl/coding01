package com.example.blankingmanage.service;

import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.mapper.InventoryFlowMapper;
import com.example.blankingmanage.mapper.MaterialMapper; // 假设你有这个物品主表的Mapper
import com.example.blankingmanage.service.InventoryAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service("simpleAuditService")
public class SimpleAuditService implements InventoryAuditService {

    @Autowired
    private InventoryFlowMapper flowMapper;

    @Autowired
    private MaterialMapper materialMapper; // 操纵物品主表

    // 🌟 防御装甲 1：必须加事务！一旦改库存失败，流水状态必须回滚！
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAudit(Long flowId, Integer action, Long auditUserId, String remark) {
        InventoryFlow flow = flowMapper.selectById(flowId);
        if (flow == null) {
            throw new RuntimeException("流水记录不存在");
        }
        // 🌟 防御装甲 2：状态机绝对防御。只能审核"待确认"的单子！防连点、防重复提交！
        if (flow.getStatus() != 0) {
            throw new RuntimeException("该记录已被处理，请刷新页面");
        }
        // 1. 如果库管点的是【通过】
        if (action == 1) {
            // 🌟 核心业务：去物品主表里真实扣减/增加库存
            // 注意：这里最好写一个基于数据库行锁的乐观锁更新语句（如：update stock = stock - num where id = ? and stock >= num）
            // 严禁在代码里查出来算好再 update，高并发下必出现超卖！
            int rows = materialMapper.updateRealStock(flow.getMaterialId(), flow.getChangeType(), flow.getChangeNum());
            if (rows <= 0) {
                throw new RuntimeException("库存更新失败！可能是库存不足！");
            }
            flow.setStatus(1); // 1-已完成
        }
        // 2. 如果库管点的是【驳回】
        else if (action == 2) {
            flow.setStatus(2); // 2-已驳回
        } else {
            throw new IllegalArgumentException("未知操作指令");
        }
        // 3. 落库：更新流水表状态和审核人信息
        flow.setAuditUserId(auditUserId);
        flow.setAuditTime(LocalDateTime.now());
        flow.setRemark(remark);
        flowMapper.updateById(flow);
        log.info("审核流水完毕. flowId={}, action={}, auditUser={}", flowId, action, auditUserId);
    }
}
