package chapter2.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chapter2.LiftOff;

/**
 * @ClassNAME CachedThreadPool
 * @Description 使用执行器（Executor）管理Thread对象
 * @Author yu
 * @Date 2023/8/23 16:25
 * @Version 1.0
 */
public class CachedThreadPool {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new LiftOff());
        }

        exec.shutdown();
    }
}
