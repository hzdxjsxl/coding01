package com.example.blankingmanage.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blankingmanage.entity.ErrorLog;
import com.example.blankingmanage.entity.OperationLog;
import com.example.blankingmanage.mapper.ErrorLogMapper;
import com.example.blankingmanage.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService  extends ServiceImpl<ErrorLogMapper, ErrorLog> {

}
