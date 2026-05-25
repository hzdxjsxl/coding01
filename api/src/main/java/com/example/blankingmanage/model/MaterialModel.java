package com.example.blankingmanage.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialModel {
    private String materialName;
    private String materialType;
    private BigDecimal stockNum;
    private String purpose;
    private String address;
}
