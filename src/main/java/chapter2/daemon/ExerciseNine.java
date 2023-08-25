package chapter2.daemon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseNine
 * @Description 21.2.8 练习 9 使定制的ThreadFactory可以设置线程的优先级
 * @Author yu
 * @Date 2023/8/23 18:11
 * @Version 1.0
 */
public class ExerciseNine implements Runnable {
    private int countDown = 5;
    private volatile double d;
    private int priority;

    public ExerciseNine(int priority) {
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
            exec.execute(new ExerciseNine(Thread.MIN_PRIORITY));
        }

        exec.execute(new ExerciseNine(Thread.MAX_PRIORITY));

        exec.shutdown();
    }
}
