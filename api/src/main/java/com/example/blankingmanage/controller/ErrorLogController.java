package com.example.blankingmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.entity.ErrorLog;
import com.example.blankingmanage.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/errorLog")
public class ErrorLogController {
    @Autowired
    private ErrorLogService errorLogService;

    /**
     * 分页查询报错日志列表
     * @param current 当前页码
     * @param size    每页条数
     * @param uri     (可选) 按接口路径模糊查询
     */
    @GetMapping("/list")
    public ApiResponse<?> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String uri) {

        Page<ErrorLog> page = new Page<>(current, size);
        LambdaQueryWrapper<ErrorLog> wrapper = new LambdaQueryWrapper<>();

        // 如果想按 URI 搜，就加上模糊查询
        if (StringUtils.hasText(uri)) {
            wrapper.like(ErrorLog::getRequestUri, uri);
        }

        // 🌟 必须按时间倒序，最新的 Bug 永远排在最前面！
        wrapper.orderByDesc(ErrorLog::getCreateTime);

        // 为了列表页加载快，把那个长达几千行的堆栈字段过滤掉，不查出来
        wrapper.select(ErrorLog.class, info -> !info.getColumn().equals("error_stack"));

        Page<ErrorLog> result = errorLogService.page(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 获取报错详情 (点开弹窗看完整的报错堆栈)
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<?> detail(@PathVariable Long id) {
        ErrorLog log = errorLogService.getById(id);
        return ApiResponse.success(log);
    }
}
