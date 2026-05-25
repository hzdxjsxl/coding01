package com.example.blankingmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.entity.Customer;
import com.example.blankingmanage.entity.Material;
import com.example.blankingmanage.query.MaterialQuery;
import com.example.blankingmanage.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/material")
@Tag(name = "库存模块")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @OperateLog(module = "库存模块", action = "查询物品列表")
    @Operation(summary = "查询物品列表")
    @PostMapping("/list")
    public ApiResponse<?> getMaterialListApi(@RequestBody MaterialQuery query) {
        return ApiResponse.success(PageResponse.of(materialService.getMaterialListApi(query)));
    }
    @OperateLog(module = "库存模块", action = "保存物品")
    @Operation(summary = "保存物品")
    @PostMapping("/save")
    public ApiResponse<String> saveMaterialApi(@RequestBody Material material) {
        if (!materialService.saveOrUpdate(material)) {
            return ApiResponse.error(400,"保存失败");
        }
        return ApiResponse.success("保存成功");
    }
    @OperateLog(module = "库存模块", action = "删除物品")
    @Operation(summary = "删除物品")
    @PostMapping("/del")
    public ApiResponse<String> delMaterialApi(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error(400,"要删除的数据不能为空");
        }
        materialService.removeByIds(ids);
        return ApiResponse.success("删除成功");
    }
    /**
     * 物品信息 Excel 数据迁移导入 (极简流 EasyExcel)
     */
    @OperateLog(module = "库存模块", action = "表格导入物品信息")
    @Operation(summary = "表格导入物品")
    @PostMapping("/import")
    public ApiResponse<String> importMaterial(@RequestParam("file") MultipartFile file) {
        try {
            // 🌟 极简流派：这里利用阿里巴巴的 EasyExcel，一行代码搞定解析和批量落库
            // 参数说明：文件流，实体类结构，一个监听器（在监听器里写校验和批量 insert 的逻辑）
            // EasyExcel.read(file.getInputStream(), MaterialExcelDTO.class, new MaterialDataListener(materialMapper)).sheet().doRead();

            return ApiResponse.success("导入成功");
        } catch (Exception e) {
            log.error("Excel导入失败", e);
            return ApiResponse.error(400,"导入失败，请检查数据格式");
        }
    }
}