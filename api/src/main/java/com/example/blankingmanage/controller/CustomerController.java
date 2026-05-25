package com.example.blankingmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.entity.User;
import com.example.blankingmanage.query.CustomerQuery;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.entity.Customer;
import com.example.blankingmanage.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "客户模块")
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @OperateLog(module = "客户模块", action = "查询客户列表")
    @Operation(summary = "查询客户列表")
    @PostMapping("/list")
    public ApiResponse<?> getListApi(@RequestBody CustomerQuery query) {
        return ApiResponse.success(PageResponse.of(customerService.getListApi(query)));
    }
    @OperateLog(module = "客户模块", action = "保存客户")
    @Operation(summary = "保存客户")
    @PostMapping("/save")
    public ApiResponse<String> saveCustomerApi(@RequestBody Customer customer) {
        if (!customerService.saveOrUpdate(customer)) {
            return ApiResponse.error(400,"保存失败");
        }
        return ApiResponse.success("保存成功");
    }
    @OperateLog(module = "客户模块", action = "删除客户")
    @Operation(summary = "删除客户")
    @PostMapping("/del")
    public ApiResponse<String> delete(@RequestBody List<Long> ids) {
        // 直接物理传参极简删除，底层会自动转换为逻辑删除 (is_deleted = 1)
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error(400,"要删除的数据不能为空");
        }
        customerService.removeByIds(ids);
        return ApiResponse.success("删除成功");
    }
    @OperateLog(module = "客户模块", action = "查询所有客户不分页")
    @Operation(summary = "查询所有客户不分页")
    @PostMapping("/listAll")
    public ApiResponse<List<Customer>> listAll() {
        // 专门给后续【订单开单界面】下拉框用的接口，直接全量拉取，不做分页
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Customer::getId, Customer::getCompanyName, Customer::getContactName, Customer::getPhone)
                .orderByDesc(Customer::getCreateTime);
        return ApiResponse.success(customerService.list(wrapper));
    }
}