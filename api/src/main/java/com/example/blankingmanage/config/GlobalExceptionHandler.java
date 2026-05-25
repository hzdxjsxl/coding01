package com.example.blankingmanage.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.example.blankingmanage.common.ApiResponse;
import com.example.blankingmanage.common.ServiceException;
import com.example.blankingmanage.common.utils.LoginUserUtils;
import com.example.blankingmanage.entity.ErrorLog;
import com.example.blankingmanage.service.ErrorLogService;
import com.example.blankingmanage.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 假设你已经建好了 SysLog 的 Service
     @Autowired
     private ErrorLogService errorLogService;
     @Autowired
     @Qualifier("logThreadPool") // 假设你配了这样一个线程池
     private Executor logExecutor;
    @ExceptionHandler(ServiceException.class)
    public ApiResponse<?> handleServiceException(ServiceException e) {
        // 直接把异常里的 message 返回给前端展示，干干净净
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
    // ================= 拦截第一道防线：参数校验报错 =================
    // 刚刚咱们加的 @NotBlank，如果没通过，会抛出这个异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        // 直接抽出咱们写的 message ("用户名绝对不能为空")
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        // 注意：这种属于用户自己填错的“业务预期内错误”，不需要存进数据库污染日志表！
        return ApiResponse.error(400, msg);
    }

    // ================= 拦截第二道防线：Sa-Token 权限报错 =================
    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<?> handleNotLoginException(NotLoginException e) {
        // 同样，用户没登录或者 Token 过期，直接告诉前端去登录界面，不存库
        return ApiResponse.error(401, "登录已失效，请重新登录");
    }

    // ================= 拦截终极防线：未知系统崩溃（记入日志表的核心！） =================
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统崩溃抓取: URL: {}, 报错: {}", request.getRequestURI(), e.getMessage(), e);

        // 🌟 1. 主线程提前提取所有上下文数据 (切断与 Request 和 ThreadLocal 的联系)
        final String uri = request.getRequestURI();

        Long tempUserId = null;
        String tempUserName = null;

        // 🌟 2. 真正的“生吞”：铁壁防御
        try {
            if (StpUtil.isLogin()) {
                tempUserId = LoginUserUtils.getUserId();
                tempUserName = LoginUserUtils.getCurrentUser().getUsername();
            }
        } catch (Exception ignored) {
            // 彻底生吞：即使 Redis 挂了，也要保证后续的报错日志能落盘！
        }
        // 🌟 3. 提取完整的报错堆栈信息 (重要！)
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String fullStackTrace = sw.toString();
        // 🔪 Tom 补丁：最长只取前 8000 个字符，防止 MySQL 炸库
        if (fullStackTrace.length() > 8000) {
            fullStackTrace = fullStackTrace.substring(0, 8000) + "\n...[堆栈过长已截断]...";
        }
        // 将变量定死，传给子线程
        final Long finalUserId = (tempUserId != null) ? tempUserId : 0L;
        final String finalUserName = (tempUserName != null) ? tempUserName : "匿名/未登录";
        final String finalStackTrace = fullStackTrace;

        // 🌟 4. 异步落库：小弟只管拿死数据干活，绝不碰任何上下文！
        CompletableFuture.runAsync(() -> {
            //Java 异步编程里最容易踩的“惊天大坑”：CompletableFuture 是一个“哑巴”！
            //如果你在 runAsync 里面没有严丝合缝地加上 try-catch（或者你刚才复制代码时漏掉了 catch 块），
            //异步线程里抛出的所有异常，都会被 CompletableFuture 完全吞噬，死寂无声，绝不会打印到主线程的控制台上！
            try {
                ErrorLog errorLog = new ErrorLog();
                errorLog.setRequestUri(uri);
                // 写入绝对不为 null 的值
                errorLog.setRequestUserId(finalUserId);
                errorLog.setRequestUserName(finalUserName);

                String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
                errorLog.setErrorBrief(errorMsg.length() > 500 ? errorMsg.substring(0, 500) : errorMsg);
                errorLog.setErrorStack(finalStackTrace);

                errorLogService.save(errorLog);

            } catch (Exception ex) {
                log.error("致命故障！报错日志写入数据库失败！原始报错如下：", ex);
            }
        },logExecutor);

        // 5. 安抚前端
        return ApiResponse.error(500, "系统开小差了，请联系管理员");
    }
}
