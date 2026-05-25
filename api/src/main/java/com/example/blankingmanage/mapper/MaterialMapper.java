package com.example.blankingmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    List<Material> list();
    /**
     * 极度致命：底层并发扣减库存
     * @param materialId 物品ID
     * @param changeType 变动类型: 1-入库, 2-出库
     * @param changeNum 变动数量
     * @return 影响行数 (如果为0说明库存不足)
     */
    int updateRealStock(@Param("materialId") Long materialId,
                        @Param("changeType") Integer changeType,
                        @Param("changeNum") BigDecimal changeNum);

    @Select("SELECT * FROM sys_material WHERE id = #{materialId} FOR UPDATE")
    Material selectByIdForUpdate(@Param("materialId") Long materialId);
}