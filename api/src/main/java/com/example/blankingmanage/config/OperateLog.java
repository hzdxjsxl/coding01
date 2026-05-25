package com.example.blankingmanage.config;

import java.lang.annotation.*;
@Target(ElementType.METHOD) // 作用在接口方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
    String module() default ""; // 比如："库存管理"
    String action() default ""; // 比如："出库扣减"
}