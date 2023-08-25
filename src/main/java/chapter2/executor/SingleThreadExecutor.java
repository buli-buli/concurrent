package chapter2.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chapter2.LiftOff;

/**
 * @ClassNAME SingleThreadExecutor
 * @Description SingleThreadExecutor就像是线程数量为1的FixedThreadPool
 * @Author yu
 * @Date 2023/8/23 16:33
 * @Version 1.0
 */
public class SingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 5; i++) {
            exec.execute(new LiftOff());
        }

        exec.shutdown();
    }
}
