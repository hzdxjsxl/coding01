package com.example.blankingmanage.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.entity.Order;
import com.example.blankingmanage.model.OrderModel;
import com.example.blankingmanage.query.OrderQuery;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "订单模块")
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @OperateLog(module = "订单模块", action = "查询订单列表")
    @Operation(summary = "查询订单列表")
    @PostMapping("/list")
    public ApiResponse<?> getListApi(@RequestBody OrderQuery query) {
        return ApiResponse.success(PageResponse.of(orderService.getListApi(query)));
    }
    @OperateLog(module = "订单模块", action = "保存/修改订单")
    @Operation(summary = "保存/修改订单")
    @PostMapping("/save")
    public ApiResponse<?> saveOrderApi(@RequestBody OrderModel model) {
        return orderService.saveOrder(model);
    }
    @OperateLog(module = "订单模块", action = "批量删除订单")
    @Operation(summary = "批量删除订单")
    @PostMapping("/del") // 👈 路径要求：del
    public ApiResponse<?> delOrderApi(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error(400,"要删除的数据不能为空");
        }
        return orderService.removeByIds(ids) ? ApiResponse.success("删除成功") : ApiResponse.error(400,"删除失败");
    }
    @OperateLog(module = "订单模块", action = "修改订单完成状态")
    @Operation(summary = "修改订单完成状态")
    @PostMapping("/updateStatus") // 👈 路径要求：del
    public ApiResponse<?> updateStatus(@RequestBody OrderQuery query) {
        if (query.getIds() == null || query.getIds().isEmpty()) {
            return ApiResponse.error(400,"要修改的数据不能为空");
        }
        return orderService.updateStatus(query) ? ApiResponse.success("删除成功") : ApiResponse.error(400,"删除失败");
    }
    @OperateLog(module = "订单模块", action = "导出订单列表")
    @Operation(summary = "导出订单列表")
    @GetMapping("/export")
    public void exportOrderApi(HttpServletResponse response,@RequestBody OrderQuery query) throws IOException {
        List<Order> orderList = orderService.getListApi(query).getRecords();
        List<OrderModel> exportList = orderList.stream().map(order -> {
            OrderModel vo = new OrderModel();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusText(order.getStatus() == 0 ? "未开始" : order.getStatus() == 1 ? "已完成":"进行中");
            return vo;
        }).collect(Collectors.toList());
        EasyExcel.write(response.getOutputStream(), OrderModel.class)
                .sheet("订单数据")
                .doWrite(exportList);
    }
}