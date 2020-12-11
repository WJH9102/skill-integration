package cn.junhaox.espractice.utils;

import java.util.concurrent.*;

/**
 * @Author WJH
 * @Description
 * @date 2020/12/10 9:24
 * @Email ibytecode2020@gmail.com
 */
public enum EnumThreadPool {

    /**
     * 单例枚举线程池
     */
    THREAD_POOL;

    /**
     * 枚举属性满足单例
     */
    private final ThreadPoolExecutor poolExecutor;
    private final ThreadFactory factory;

    EnumThreadPool() {
        //根据cpu的数量动态的配置核心线程数和最大线程数
        int cpuCount = Runtime.getRuntime().availableProcessors();
        //核心线程数 = CPU核心数 + 1
        int corePoolSize    = cpuCount + 1;
        //线程池最大线程数 = CPU核心数 * 2 + 1
        int maximumPoolSize = cpuCount * 2 + 1;
        //非核心线程闲置时超时1s
        int keepAlive = 1;
        // 线程工程
        factory = Executors.defaultThreadFactory();
        // 线程队列
        LinkedBlockingQueue<Runnable> blockQueue = new LinkedBlockingQueue<>();
        poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAlive, TimeUnit.SECONDS, blockQueue, factory);
    }

    public ThreadFactory getThreadFactory() {
        return factory;
    }

    public ThreadPoolExecutor getInstance() {
        return this.poolExecutor;
    }

}
