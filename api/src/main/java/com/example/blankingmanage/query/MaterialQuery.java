package com.example.blankingmanage.query;

import com.example.blankingmanage.common.DTO.BasePageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MaterialQuery extends BasePageDTO {
    private String materialName;
    private String materialType;
    private BigDecimal stockNum;
    private String purpose;
    private String address;
}
