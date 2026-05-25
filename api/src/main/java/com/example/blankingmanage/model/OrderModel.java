package com.example.blankingmanage.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Material;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
public class OrderModel {
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS精度丢失
    private Long customerId;
    @ExcelProperty("客户姓名")
    private String contactName;
    @ExcelProperty("客户公司")
    private String companyName;
    @ExcelProperty("客户联系方式")
    private String phone;
    @ExcelProperty("订单说明")
    private String orderName;
    @ExcelProperty("总金额")
    private BigDecimal totalAmount;
    @ExcelProperty("已支付金额")
    private BigDecimal paidAmount;
    @ExcelProperty("未支付金额")
    private BigDecimal unpaidAmount;
    @ExcelProperty("下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;
    @ExcelProperty("交付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deliveryTime;
    private Integer status;
    @ExcelProperty("完成状态")
    private String statusText;
    //库存变动信息
    private List<InventoryFlow> inventoryFlowList;




}
