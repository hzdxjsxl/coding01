package com.example.blankingmanage.query;

import com.example.blankingmanage.common.DTO.BasePageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderQuery extends BasePageDTO {
    private String contactName;
    private String companyName;
    private String orderName;
    private List<String> orderTime;
    private List<String> deliveryTime;
    private Integer status;
    private Integer noPage;
    private List<Long> ids;
}
