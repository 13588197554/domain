package com.fly.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class TheadPoolConfig {

    @Value("${POOL_SIZE}")
    private int poolSize;
    @Value("${MAX_POOL_SIZE}")
    private int maxPoolSize;
    @Value("${QUEUE_CAPACITY}")
    private int queueCapacity;
    @Value("${KEEP_ALIVE_SECONDS}")
    private int keepAliveSeconds;

    @Bean
    public Executor taskPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(poolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setThreadNamePrefix("HASYOU_");
        return pool;
    }

}
