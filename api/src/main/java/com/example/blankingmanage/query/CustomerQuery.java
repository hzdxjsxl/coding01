package com.example.blankingmanage.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.blankingmanage.common.DTO.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerQuery extends BasePageDTO {
    private String keyword;
    private String companyName;
    private String contactName;
    private String phone;
    private String address;
    private List<String> createTime;
}
