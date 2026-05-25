package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.common.utils.LoginUserUtils;
import com.example.blankingmanage.entity.Customer;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Material;
import com.example.blankingmanage.entity.Order;
import com.example.blankingmanage.mapper.CustomerMapper;
import com.example.blankingmanage.mapper.InventoryFlowMapper;
import com.example.blankingmanage.mapper.MaterialMapper;
import com.example.blankingmanage.mapper.OrderMapper;
import com.example.blankingmanage.model.OrderModel;
import com.example.blankingmanage.query.OrderQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;
    private final InventoryFlowMapper inventoryFlowMapper;
    private final MaterialMapper materialMapper;


    public Page<Order> getListApi(OrderQuery query) {
        //拦截 noPage 标识，如果是不分页，强行把 pageSize 改成 -1
        long size = (query.getNoPage() != null && query.getNoPage() == 1) ? -1 : query.getPageSize();
        Page<Order> page = new Page<>(query.getPageIndex(), size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getOrderName())) {
            wrapper.like(Order::getOrderName, query.getOrderName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getCompanyName())) {
            wrapper.like(Order::getCompanyName, query.getCompanyName());
        }
        if (StringUtils.hasText(query.getContactName())) {
            wrapper.like(Order::getContactName, query.getContactName());
        }
        if (query.getOrderTime() != null && query.getOrderTime().size() == 2) {
            wrapper.ge(Order::getOrderTime, query.getOrderTime().get(0))
                    .le(Order::getOrderTime, query.getOrderTime().get(1));
        }
        if (query.getDeliveryTime() != null && query.getDeliveryTime().size() == 2) {
            wrapper.ge(Order::getDeliveryTime, query.getDeliveryTime().get(0))
                    .le(Order::getDeliveryTime, query.getDeliveryTime().get(1));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        wrapper.select(Order::getId, Order::getCustomerId, Order::getContactName, Order::getCompanyName, Order::getPhone, Order::getOrderName, Order::getTotalAmount, Order::getPaidAmount, Order::getUnpaidAmount, Order::getOrderTime, Order::getDeliveryTime, Order::getStatus);
        return this.page(page, wrapper);
    }
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<?> saveOrder(OrderModel model) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Customer::getContactName,Customer::getCompanyName,Customer::getPhone).eq(Customer::getId,model.getCustomerId());
        wrapper.last("LIMIT 1");
        Customer customer = customerMapper.selectOne(wrapper);
        if (customer != null) {
            //客户快照
            model.setContactName(customer.getContactName());
            model.setCompanyName(customer.getCompanyName());
            model.setPhone(customer.getPhone());
        }
        if (model.getTotalAmount() != null && model.getPaidAmount() != null) {
            //计算未支付金额
            model.setUnpaidAmount(model.getTotalAmount().subtract(model.getPaidAmount()));
        }
        Order order = new Order();
        BeanUtils.copyProperties(model,order);
        this.saveOrUpdate(order);
        //健壮性防线：判空兜底
        List<InventoryFlow> inventoryFlowList = model.getInventoryFlowList();
        if (inventoryFlowList == null || inventoryFlowList.isEmpty()) {
            return ApiResponse.success("保存成功");
        }
        //按物品 ID 排序，彻底杜绝多线程循环加锁导致的死锁！
        inventoryFlowList.sort(Comparator.comparing(InventoryFlow::getMaterialId));
        //执行物品库存计算List<InventoryFlow> inventoryFlowList = model.getInventoryFlowList();
        for (InventoryFlow item : model.getInventoryFlowList()) {
            //1.加锁查询最新库存
            Material material = materialMapper.selectByIdForUpdate(item.getMaterialId());
            if (material == null) {
                throw new ServiceException("物品不存在");
            }
            BigDecimal beforeNum = material.getStockNum();
            BigDecimal changeNum = item.getChangeNum();
            //2.检测库存是否超卖
            if (beforeNum.compareTo(changeNum) < 0) {
                throw new ServiceException("物品 [" + material.getMaterialName() + "] 库存不足！当前仅剩: " + material.getStockNum() + material.getUnit());
            }
            //3.计算 afterNum
            BigDecimal afterNum = beforeNum.subtract(changeNum);
            //4.执行扣减
            material.setStockNum(afterNum);
            materialMapper.updateById(material);
            //5.提交库存流水
            InventoryFlow flow = new InventoryFlow();
            flow.setMaterialId(material.getId());
            flow.setChangeType(2);
            flow.setBeforeNum(beforeNum);
            flow.setChangeNum(changeNum);
            flow.setAfterNum(afterNum);
            flow.setRelationOrderNo(order.getId().toString());
            flow.setCreateUserId(LoginUserUtils.getUserId());
            flow.setCreateTime(LocalDateTime.now());
            flow.setMaterialName(material.getMaterialName());
            flow.setMaterialType(material.getMaterialType());
            flow.setCreateUserName(LoginUserUtils.getCurrentUser().getUsername());
            inventoryFlowMapper.insert(flow);
        }
        return ApiResponse.success("保存成功");
    }
    public boolean updateStatus(OrderQuery query) {
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Order::getId, query.getIds())
                .set(Order::getStatus, query.getStatus());
        orderMapper.update(updateWrapper);
        return true;
    }
}