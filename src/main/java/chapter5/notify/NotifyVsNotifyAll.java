package chapter5.notify;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME NotifyVsNotifyAll
 * @Description 当notifyAll()因某个特定锁而被调用时，只有等待这个锁的任务才会被唤醒
 * @Author yu
 * @Date 2023/8/28 21:19
 * @Version 1.0
 */
public class NotifyVsNotifyAll {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 5; i++) {
            exec.execute(new Task());
        }

        exec.execute(new Task2());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            boolean prod = true;

            @Override
            public void run() {
                if (prod) {
                    System.out.print("\n notify() ");
                    Task.blocker.prod();
                    prod = false;
                } else {
                    System.out.print("\n notifyAll()");
                    Task.blocker.prodAll();
                    prod = true;
                }
            }
        }, 400, 400);// 每4/10秒运行一次

        TimeUnit.SECONDS.sleep(5);
        timer.cancel();
        System.out.println("\n Timer canceled");

        TimeUnit.MILLISECONDS.sleep(500);
        Task2.blocker.prodAll();;

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("\n Shutting down");

        exec.shutdownNow();
    }
}

class Blocker {
    synchronized void waitingCall() {
        try {
            while (!Thread.interrupted()) {
                wait();
                System.out.print(Thread.currentThread() + " ");
            }
        } catch (InterruptedException e) {
            // OK to exit this way
            System.out.println("InterruptedException");
        }
    }

    synchronized void prod() {
        notify();
    }

    synchronized void prodAll() {
        notifyAll();
    }
}

class Task implements Runnable {
    static Blocker blocker = new Blocker();

    @Override
    public void run() {
        blocker.waitingCall();
    }
}

class Task2 implements Runnable {
    static Blocker blocker = new Blocker();

    @Override
    public void run() {
        blocker.waitingCall();
    }
}
