package com.gushici.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

public class ThreadPool {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static Logger logger = LoggerFactory.getLogger(ThreadPool.class);

    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1000);

    private static final ExecutorService executor = new ThreadPoolExecutor(
            CPU_COUNT+1,
            //CPU_COUNT*2+1,      //CPU密集型
            10,   //IO密集型  CPU_COUT/(1-0.9)
            60L,
            TimeUnit.SECONDS,
            queue,
            (r, executor) -> {
                try {
                    executor.getQueue().put(r);
                } catch (Exception e) {
                    logger.error("线程池入队异常", e);
                }
            });

    public static ExecutorService newExecutorInstance() {
        logger.info("获取线程池 CPU个数为{}", CPU_COUNT);
        return executor;
    }

}
