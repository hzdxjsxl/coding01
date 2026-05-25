package com.example.blankingmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.entity.InventoryFlow;
import com.example.blankingmanage.entity.Material;
import com.example.blankingmanage.mapper.MaterialMapper;
import com.example.blankingmanage.query.MaterialQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class MaterialService extends ServiceImpl<MaterialMapper, Material> {

    public Page<Material> getMaterialListApi(MaterialQuery query) {
        long size = (query.getNoPage() != null && query.getNoPage() == 1) ? -1 : query.getPageSize();
        Page<Material> page = new Page<>(query.getPageIndex(), size);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getMaterialName())) {
            wrapper.like(Material::getMaterialName, query.getMaterialName());
        }
        if (StringUtils.hasText(query.getAddress())){
            wrapper.like(Material::getAddress, query.getAddress());
        }
        wrapper.orderByDesc(Material::getCreateTime);
        wrapper.select(Material::getId,
                Material::getMaterialName,
                Material::getMaterialType,
                Material::getStockNum,
                Material::getUnit,
                Material::getDescription,
                Material::getDescription,
                Material::getPurpose,
                Material::getAddress,
                Material::getLocationImage,
                Material::getRemark
        );
        return this.page(page, wrapper);
    }
}