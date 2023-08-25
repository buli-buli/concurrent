package chapter2.daemon;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME DaemonThreadFactory
 * @Description 实现 ThreadFactory，将所有进程后台状态设置为 true
 * @Author yu
 * @Date 2023/8/23 22:30
 * @Version 1.0
 */
public class DaemonFromFactory implements Runnable {
    @Override
    public void run() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println(Thread.currentThread() + " " + this);
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool(new DaemonThreadFactory());

        for (int i = 0; i < 10; i++) {
            exec.execute(new DaemonFromFactory());
        }

        System.out.println("All daemon started");
        TimeUnit.MILLISECONDS.sleep(500);
    }
}

class DaemonThreadFactory implements ThreadFactory {
    private Random r = new Random();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setPriority(this.r.nextInt(10) + 1);
        return t;
    }
}
