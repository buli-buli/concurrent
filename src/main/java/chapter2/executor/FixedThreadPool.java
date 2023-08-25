package chapter2.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chapter2.LiftOff;

/**
 * @ClassNAME FixedThreadPool
 * @Description 预先执行代价高昂的线程分配，限制线程的数量
 * @Author yu
 * @Date 2023/8/23 16:34
 * @Version 1.0
 */
public class FixedThreadPool {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            exec.execute(new LiftOff());
        }
        exec.shutdown();
    }
}
