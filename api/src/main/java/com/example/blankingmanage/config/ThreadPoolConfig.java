package com.example.blankingmanage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    // 🌟 给这个池子起个代号，方便后面按需调用
    @Bean("logThreadPool")
    public Executor logThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. 核心兄弟数：随时待命的底层打工人（一般等于 CPU 核心数，咱们系统不大，4个够了）
        executor.setCorePoolSize(4);

        // 2. 最大兄弟数：遇到大面积报错（比如数据库宕机），最多能喊多少人来帮忙
        executor.setMaxPoolSize(10);

        // 3. 队列容量：兄弟们都在忙，后面的报错任务先在队列里排队（设个 500，防内存撑爆）
        executor.setQueueCapacity(500);

        // 4. 线程前缀名（极其重要）：以后出 Bug 看控制台，一眼就知道是这帮日志兄弟干的
        executor.setThreadNamePrefix("Log-Async-Thread-");

        // 5. 💥 终极防线：拒绝策略（当 10个人全在忙，500个队列也排满时怎么办？）
        // CallerRunsPolicy：谁惹的祸谁自己处理！让主线程（Tomcat）亲自去写日志，保证绝对不丢数据。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
