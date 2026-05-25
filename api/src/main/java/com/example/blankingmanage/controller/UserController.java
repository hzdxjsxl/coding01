package com.example.blankingmanage.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blankingmanage.query.LoginQuery;
import com.example.blankingmanage.config.OperateLog;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.common.PageResponse;
import com.example.blankingmanage.entity.User;
import com.example.blankingmanage.query.UserQuery;
import com.example.blankingmanage.model.UserSaveModel;
import com.example.blankingmanage.model.UserUpModel;
import com.example.blankingmanage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//2026 4.14 0点46 登录接口还需要密码的校验（最少6位或者数字符号字母搭配），以及验证码输入防止暴露测试。
/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 代码大兵Jack
 * @since 2026-01-28
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @OperateLog(module = "用户模块", action = "系统登录")
    @Operation(summary = "用户登录")
    @SaIgnore
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginQuery loginForm) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginForm.getUsername())
                .eq(User::getPassword, userService.getSalt(loginForm.getPassword()))
                .eq(User::getStatus, 1);
        User user = userService.getOne(wrapper);
        if (user != null) {
            // sa-taken底层自动生成 Token 并接管状态
            StpUtil.login(user.getId());
            // 给 user 对象脱敏（千万别把密码也存进缓存里泄露了！）
            user.setPassword(null);
            // 将脱敏后的用户信息，直接塞进该用户的全局 Session 中
            StpUtil.getSession().set("currentUser", user);
            // 拿到自动生成的 Token 发给前端
            Map<String, String> data = new HashMap<>();
            data.put("token", StpUtil.getTokenValue());

            return ApiResponse.success(data);
        } else {
            return ApiResponse.error(500, "账号或密码错误");
        }
    }
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getInfo() {
        // 🌟 核心魔法：一行代码判断是否登录，没登录底层直接抛异常被拦截
        StpUtil.checkLogin();

        // 拿取当前登录的账号 ID (刚才 login 时存进去的)
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("avatar", user.getAvatar());
        data.put("roles", Arrays.asList("admin")); // 抛给前端的极简权限

        return ApiResponse.success(data);
    }

    @OperateLog(module = "用户模块", action = "退出登录")
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // 🌟 核心魔法：一行代码注销当前会话
        StpUtil.logout();
        return ApiResponse.success("退出成功");
    }
    @OperateLog(module = "用户模块", action = "保存用户")
    @Operation(summary = "保存用户")
    @PostMapping("/save")
    public ApiResponse<String> saveUserApi(@RequestBody UserSaveModel userModel) {
        if (!userService.saveUser(userModel)) {
            return ApiResponse.error(400,"保存失败");
        }
        return ApiResponse.success("保存成功");
    }
    @OperateLog(module = "用户模块", action = "编辑用户")
    @Operation(summary = "编辑用户")
    @PostMapping("/update")
    public ApiResponse<String> updateUserApi(@RequestBody @Validated UserUpModel userModel) {
        if (!userService.updateUser(userModel)) {
            return ApiResponse.error(400,"保存失败");
        }
        return ApiResponse.success("保存成功");
    }
    @OperateLog(module = "用户模块", action = "删除用户")
    @Operation(summary = "删除用户")
    @PostMapping("/del")
    public ApiResponse<String> delUserApi(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error(400,"要删除的数据不能为空");
        }
        return userService.removeByIds(ids) ? ApiResponse.success("删除成功") : ApiResponse.error(400,"删除失败");
    }
    @OperateLog(module = "用户模块", action = "查询用户列表")
    @Operation(summary = "查询用户列表")
    @PostMapping("/list")
    public ApiResponse<?> getListApi(@RequestBody UserQuery queryModel) {

        Page<User> page = new Page<>(queryModel.getPageIndex(), queryModel.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryModel.getUsername())) {
            wrapper.like(User::getUsername, queryModel.getUsername());
        }
        wrapper.orderByDesc(User::getCreateTime);
        wrapper.select(User::getId,User::getUsername,User::getAvatar,User::getRealName,User::getStatus);
        Page<User> result = userService.page(page, wrapper);
        return ApiResponse.success(PageResponse.of(result));
    }
}
