package com.example.blankingmanage.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.example.blankingmanage.entity.User;

public class LoginUserUtils {
    /**
     * 极速获取当前登录用户的全套信息 (直击 Redis，不查 DB)
     */
    public static User getCurrentUser() {
        // 增加一层防御，没登录直接返回 null 或者抛异常
        if (!StpUtil.isLogin()) {
            return null; // 或者抛出你的业务异常
        }
        // 直接从 Session 中捞取我们登录时存进去的对象
        return (User) StpUtil.getSession().get("currentUser");
    }

    /**
     * 顺手封装个获取 ID 的，更短更好记
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }
}
