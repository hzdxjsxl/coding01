package com.example.blankingmanage.common;

// 🌟 必须继承 RuntimeException，这样才能无缝触发 @Transactional 的自动回滚
public class ServiceException extends RuntimeException {

    private Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = 400; // 默认业务错误码
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}