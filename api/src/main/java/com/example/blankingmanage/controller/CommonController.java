package com.example.blankingmanage.controller;

import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/common")
@Tag(name = "系统公共接口")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @OperateLog(module = "公共模块", action = "图片上传")
    @PostMapping("/upload")
    @Operation(summary = "极速文件上传(直通OSS)")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 防御性拦截：空文件直接踢回
            if (file.isEmpty()) {
                return ApiResponse.error(400, "上传文件不能为空");
            }
            // 🌟 核心绝杀：调用 Service 直传云端，拿到绝对路径 URL
            String fileUrl = commonService.uploadFile(file);
            // 返回给前端，vue-element-plus-admin 会自动把这个 data 塞进表单
            return ApiResponse.success(fileUrl);

        } catch (Exception e) {
            log.error("文件上传发生严重异常", e);
            return ApiResponse.error(500, "文件上传失败，请联系管理员");
        }
    }
}
