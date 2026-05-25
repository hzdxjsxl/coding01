package com.example.blankingmanage.config;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.example.blankingmanage.common.utils.LoginUserUtils;
import com.example.blankingmanage.entity.OperationLog;
import com.example.blankingmanage.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
public class SysLogAspect {

    @Autowired
    private OperationLogMapper logMapper;

    @Pointcut("@annotation(com.example.blankingmanage.config.OperateLog)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Long currentUserId = 0L;
        String currentUserName = "未登录/匿名用户";
        Object result = null;
        String errorMsg = null;
        int status = 1;
        try {
            if (StpUtil.isLogin()) { // 先判断一下，防止未登录接口报错
                currentUserId = LoginUserUtils.getUserId();
                currentUserName = LoginUserUtils.getCurrentUser().getUsername();
            }
        } catch (Exception e) {
            log.warn("无法获取当前操作人", e);
        }
        // 🌟 核心修复：在主线程提前把 Request 里的数据“摘”出来变成纯字符串
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        final String ip = request != null ? getIpAddress(request) : "未知IP";
        final String uri = request != null ? request.getRequestURI() : "未知URI";
        final String userAgent = request != null ? request.getHeader("User-Agent") : "未知设备";

        try {
            // 放行主业务
            result = point.proceed();
        } catch (Exception e) {
            status = 0;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            int finalStatus = status;
            String finalError = errorMsg;
            // 🌟 核心修复：把提取好的字符串纯文本传给子线程，彻底摆脱 Request 对象的生命周期限制
            Long finalCurrentUserId = currentUserId;
            String finalCurrentUserName = currentUserName;
            CompletableFuture.runAsync(() -> saveLog(point, finalStatus, finalError, ip, uri, userAgent, finalCurrentUserId, finalCurrentUserName));
        }
        return result;
    }

    // 注意：这里的参数增加了 ip, uri, userAgent
    private void saveLog(ProceedingJoinPoint point, int status, String errorMsg, String ip, String uri, String userAgent,Long currentUserId,String currentUserName) {
        try {
            OperationLog logObj = new OperationLog();

            // 1. 获取注解中的模块和动作名
            MethodSignature signature = (MethodSignature) point.getSignature();
            OperateLog operateLog = signature.getMethod().getAnnotation(OperateLog.class);
            if (operateLog != null) {
                logObj.setModule(operateLog.module());
                logObj.setActionType(operateLog.action());
            }

            // 2. 直接使用主线程传进来的字符串
            logObj.setIpAddress(ip);
            logObj.setUserAgent(userAgent);
            logObj.setRequestUrl(uri);

            logObj.setStatus(status);
            logObj.setErrorMsg(errorMsg);

            // 4. 暴力序列化请求参数防抵赖 (注意：如果在传参里直接塞了 request 对象，Fastjson 会报错，这里做了容错)
            Object[] args = point.getArgs();
            if (args.length > 0) {
                try {
                    logObj.setRequestParams(JSON.toJSONString(args[0]));
                } catch (Exception e) {
                    logObj.setRequestParams("参数包含不可序列化对象");
                    log.error("暴力序列化请求参数防抵赖",e);

                }
            }

            // 5. 绑定操作人
            logObj.setOperatorId(currentUserId);
            logObj.setOperatorName(currentUserName);
            // 6. 落库
            logMapper.insert(logObj);
        } catch (Exception e) {
            log.error("系统操作日志记录失败", e);
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}