package com.example.blankingmanage.controller;

import com.example.blankingmanage.entity.OperationLog;
import com.example.blankingmanage.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/OperationLog")
@Tag(name = "系统日志接口")
public class OperationLogController {

    @Autowired
    private OperationLogService logService;

    @GetMapping("/list")
    @Operation(summary = "查询日志列表")
    public List<OperationLog> list() {
        // 配合咱们之前手写的 XML 里的专业级 list 语句
        return logService.getBaseMapper().list();
    }

    // 新增日志的动作交给了 AOP 拦截器，这里其实都不需要写 save 接口
}