package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Order;
import com.example.blankingmanage.mapper.InventoryFlowMapper;
import com.example.blankingmanage.query.InventoryFlowQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class InventoryFlowService extends ServiceImpl<InventoryFlowMapper, InventoryFlow> {
    public Page<InventoryFlow> getInventoryFlowListApi(InventoryFlowQuery query) {
        //拦截 noPage 标识，如果是不分页，强行把 pageSize 改成 -1
        long size = (query.getNoPage() != null && query.getNoPage() == 1) ? -1 : query.getPageSize();
        Page<InventoryFlow> page = new Page<>(query.getPageIndex(), size);
        LambdaQueryWrapper<InventoryFlow> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(InventoryFlow::getStatus, query.getStatus());
        }
        LocalDateTime halfYearAgo = LocalDateTime.now().minusMonths(6);
        wrapper.select(InventoryFlow::getId, InventoryFlow::getMaterialName, InventoryFlow::getMaterialType, InventoryFlow::getChangeType, InventoryFlow::getChangeNum, InventoryFlow::getBeforeNum, InventoryFlow::getAfterNum, InventoryFlow::getRelationOrderNo, InventoryFlow::getCreateUserName, InventoryFlow::getStatus, InventoryFlow::getRemark);
        wrapper.ge(InventoryFlow::getCreateTime, halfYearAgo);
        wrapper.like(StringUtils.hasText(query.getMaterialName()), InventoryFlow::getMaterialName, query.getMaterialName())
                .like(StringUtils.hasText(query.getCreateUserName()), InventoryFlow::getCreateUserName, query.getCreateUserName())
                .orderByDesc(InventoryFlow::getCreateTime);
        return this.page(page, wrapper);
    }
}