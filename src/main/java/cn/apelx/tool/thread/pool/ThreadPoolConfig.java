package cn.apelx.tool.thread.pool;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author lx
 * @since 2019/12/30 15:00
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * 公共线程池
     *
     * @return
     */
    @Bean()
    ThreadPoolTaskExecutor commonTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        executor.setCorePoolSize(10);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(10);
        //缓存队列（阻塞队列）当核心线程数达到最大时，新任务会放在队列中排队等待执行
        executor.setQueueCapacity(10);
        //线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(60 * 3);
        // 配置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 线程前缀名称
        executor.setThreadNamePrefix("");
        executor.initialize();
        return executor;
    }
}
