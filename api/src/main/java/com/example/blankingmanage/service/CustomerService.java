package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.entity.Customer;
import com.example.blankingmanage.mapper.CustomerMapper;
import com.example.blankingmanage.query.CustomerQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {
    public Page<Customer> getListApi(CustomerQuery query) {
        Page<Customer> page = new Page<>(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getCompanyName())) {
            wrapper.like(Customer::getCompanyName, query.getCompanyName());
        }
        if (StringUtils.hasText(query.getContactName())) {
            wrapper.like(Customer::getContactName, query.getContactName());
        }
        if (StringUtils.hasText(query.getAddress())) {
            wrapper.like(Customer::getAddress, query.getAddress());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Customer::getCompanyName, query.getKeyword())
                    .or()
                    .like(Customer::getContactName, query.getKeyword()));
        }
        // 极其清爽的区间查询
        if (query.getCreateTime() != null && query.getCreateTime().size() == 2) {
            wrapper.ge(Customer::getCreateTime, query.getCreateTime().get(0)) // 大于等于开始时间
                    .le(Customer::getCreateTime, query.getCreateTime().get(1)); // 小于等于结束时间
        }
        wrapper.orderByDesc(Customer::getCreateTime);
        wrapper.select(Customer::getId, Customer::getCompanyName, Customer::getContactName, Customer::getPhone, Customer::getAddress,Customer::getCreateTime);
        return this.page(page, wrapper);
    }
}