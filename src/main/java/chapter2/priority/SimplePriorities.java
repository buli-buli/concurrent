package chapter2.priority;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME SimplePriorities
 * @Description 线程优先级示例
 * @Author yu
 * @Date 2023/8/23 18:11
 * @Version 1.0
 */
public class SimplePriorities implements Runnable {
    private int countDown = 5;
    private volatile double d;
    private int priority;

    public SimplePriorities(int priority) {
        this.priority = priority;
    }

    public String toString() {
        return Thread.currentThread() + ": " + countDown + ", priority: " + Thread.currentThread().getPriority();
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(priority);

        while (true) {
            for (int i = 1; i < 100000; i++) {
                d += (Math.PI + Math.E) / (double)1;
                if (i % 1000 == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Thread.yield();
                }
            }

            System.out.println(this);

            if (--countDown == 0) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new SimplePriorities(Thread.MIN_PRIORITY));
        }

        exec.execute(new SimplePriorities(Thread.MAX_PRIORITY));

        exec.shutdown();
    }
}
