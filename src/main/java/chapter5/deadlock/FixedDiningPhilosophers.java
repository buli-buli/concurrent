package chapter5.deadlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME FixedDiningPhilosophers
 * @Description 通过破坏死锁形成的第四个条件——循环等待修复哲学家就餐死锁问题 具体的做法是：最后一个Philosopher被初始化成先拿左边的Chopstick，后拿右边的Chopstick
 * @Author yu
 * @Date 2023/8/29 16:27
 * @Version 1.0
 */
public class FixedDiningPhilosophers {
    public static void main(String[] args) throws Exception {
        test(5, 5, false);
    }

    static void test(int ponder, int size, boolean isTimeOut) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        Chopstick[] sticks = new Chopstick[size];
        for (int i = 0; i < size; i++) {
            sticks[i] = new Chopstick();
        }

        for (int i = 0; i < size; i++) {
            if (i < size - 1) {
                exec.execute(new Philosopher(sticks[i], sticks[(i + 1) % size], i, ponder));
            } else {
                exec.execute(new Philosopher(sticks[0], sticks[i], i, ponder));
            }
        }

        if (isTimeOut) {
            TimeUnit.SECONDS.sleep(5);
        } else {
            System.out.println("Press 'Enter' to quit");
            System.in.read();
        }

        exec.shutdownNow();
    }
}
