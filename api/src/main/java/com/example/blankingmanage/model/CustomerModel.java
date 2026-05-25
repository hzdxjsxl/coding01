package com.example.blankingmanage.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerModel {
    private String companyName;
    private String contactName;
    private String phone;
    private String address;
    private LocalDateTime updateTime;
}
