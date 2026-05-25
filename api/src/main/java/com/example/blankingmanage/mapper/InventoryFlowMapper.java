package com.example.blankingmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blankingmanage.entity.InventoryFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;


@Mapper
public interface InventoryFlowMapper extends BaseMapper<InventoryFlow> {
    @Update("UPDATE sys_inventory_flow SET stock = stock - #{changeNum} WHERE id = #{materialId} AND stock >= #{changeNum}")
    int reduceStock(@Param("materialId") Long materialId, @Param("getChangeNum") BigDecimal changeNum);
    // 🌟 核心：FOR UPDATE 会在事务提交前锁住这行数据，别人只能等！
    @Select("SELECT * FROM sys_inventory_flow WHERE id = #{materialId} FOR UPDATE")
    InventoryFlow selectByIdForUpdate(@Param("materialId") Long materialId);
}