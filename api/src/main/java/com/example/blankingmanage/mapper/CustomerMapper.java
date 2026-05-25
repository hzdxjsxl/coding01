package com.example.blankingmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blankingmanage.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}