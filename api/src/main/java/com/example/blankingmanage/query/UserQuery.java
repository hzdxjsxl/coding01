package com.example.blankingmanage.query;

import com.example.blankingmanage.common.DTO.BasePageDTO;
import lombok.Data;

@Data
public class UserQuery extends BasePageDTO {

    private String username;
    private String id;
}
