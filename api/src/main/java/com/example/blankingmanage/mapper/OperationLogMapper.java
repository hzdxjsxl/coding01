package com.example.blankingmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blankingmanage.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 对应咱们之前写的“绿色标准版” XML 里的查询
     * 专门用于前端的高性能查询（已在 XML 中做了按时间倒序和 Limit 1000）
     */
    List<OperationLog> list();

}