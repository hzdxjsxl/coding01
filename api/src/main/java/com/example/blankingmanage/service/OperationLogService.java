package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.entity.OperationLog;
import com.example.blankingmanage.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
    // 基础的增删改查已经全部由 ServiceImpl 提供，这里直接空着就行
}