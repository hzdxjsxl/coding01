package com.example.blankingmanage.controller;


import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.common.utils.LoginUserUtils;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Material;
import com.example.blankingmanage.model.InventoryFlowModel;
import com.example.blankingmanage.query.InventoryFlowQuery;
import com.example.blankingmanage.service.InventoryAuditService;
import com.example.blankingmanage.service.InventoryFlowService;
import com.example.blankingmanage.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Tag(name = "库存模块")
@RestController
@Slf4j
@RequestMapping("/inventory")
public class InventoryController {
    // 注入我们刚才写的简单实现类。以后如果改用 Activiti，这里换成 flowableAuditService 即可
    @Autowired
    @Qualifier("simpleAuditService")
    private InventoryAuditService auditService;
    @Autowired
    private InventoryFlowService inventoryFlowService;
    @Autowired
    private MaterialService materialService;
    /**
     * 库管审核接口 (待确认 -> 已完成/已驳回)
     */
    @OperateLog(module = "库存模块", action = "审核入出库数据")
    @Operation(summary = "审核入出库数据")
    @PostMapping("/audit")
    public ApiResponse<?> auditFlow(@RequestBody InventoryFlowModel req) {
        // 伪代码：从 Token 中获取当前登录的库管人员ID
        Long currentUserId = LoginUserUtils.getUserId();

        // 把脏活累活全甩给 Service，Controller 保持绝对干净
        auditService.executeAudit(req.getFlowId(), req.getAction(), currentUserId, req.getRemark());

        return ApiResponse.success("操作成功");
    }
    /**
     * 1. 业务员发起出入库请求 (保存)
     */
    @OperateLog(module = "库存模块", action = "业务员发起出入库请求")
    @Operation(summary = "业务员发起出入库请求")
    @PostMapping("/save")
    public ApiResponse<?> saveInventoryFlowApi(@RequestBody InventoryFlow flow) {
        flow.setCreateUserId(LoginUserUtils.getUserId());
        flow.setCreateUserName(Objects.requireNonNull(LoginUserUtils.getCurrentUser()).getUsername());
        // 捕获物品信息快照
        Material item = materialService.getById(flow.getMaterialId());
        if (item == null) {
            return ApiResponse.error(400,"物品不存在，无法发起流水");
        }
        // 快照刻录
        flow.setMaterialName(item.getMaterialName());
        flow.setMaterialType(item.getMaterialType());
        //死锁状态：发起必定是待确认
        flow.setStatus(0);
        flow.setCreateTime(LocalDateTime.now());
        inventoryFlowService.save(flow);
        return ApiResponse.success("发起成功，等待库管审核");
    }

    @OperateLog(module = "库存模块", action = "查询库存流水")
    @Operation(summary = "查询库存流水")
    @PostMapping("/list")
    public ApiResponse<?> getInventoryFlowListApi(@RequestBody InventoryFlowQuery query) {
        return ApiResponse.success(PageResponse.of(inventoryFlowService.getInventoryFlowListApi(query)));
    }
}