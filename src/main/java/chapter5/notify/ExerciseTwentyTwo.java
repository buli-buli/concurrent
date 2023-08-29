package chapter5.notify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseTwentyTwo
 * @Description 第五章 练习 22 忙等待示例
 * @Author yu
 * @Date 2023/8/28 16:01
 * @Version 1.0
 */
public class ExerciseTwentyTwo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();

        Worker1 worker1 = new Worker1("worker1");
        exec.execute(worker1);
        exec.execute(new Listener1(worker1, "listener1"));

        Worker2 worker2 = new Worker2("worker2");
        exec.execute(worker2);
        exec.execute(new Listener2(worker2, "listener2"));

        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class Worker1 implements Runnable {
    private boolean isBusy = true;
    protected String name;

    public Worker1(String name) {
        this.name = name;
    }

    public synchronized boolean isBusy() {
        return isBusy;
    }

    public synchronized void setBusy(boolean busy) {
        isBusy = busy;
    }

    public void handleStuff() {
        try {
            while (true) {
                System.out.println(name + " Handling stuffs...");
                TimeUnit.MILLISECONDS.sleep(800);
                setBusy(false);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " InterruptedException while handling");
        }
    }

    @Override
    public void run() {
        handleStuff();
    }
}

class Worker2 implements Runnable {
    protected boolean isBusy = true;
    protected String name;

    public synchronized void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Worker2(String name) {
        this.name = name;
    }

    public synchronized void work() throws InterruptedException {
        System.out.println(name + " is working...");
        TimeUnit.MILLISECONDS.sleep(800);
        isBusy = false;
        System.out.println(name + " finished working...");
        notifyAll();
    }

    public synchronized void assignJob(Listener2 listener2) throws InterruptedException {
        System.out.println(listener2 + " assigned new work");
        TimeUnit.MILLISECONDS.sleep(100);
        isBusy = true;
        notifyAll();
    }

    public synchronized void waitForStuff() throws InterruptedException {
        System.out.println("waitForStuff");
        while (isBusy == false) {
            wait();
        }
    }

    public synchronized void waitForDone() throws InterruptedException {
        System.out.println("waitForDone");
        while (isBusy == true) {
            wait();
        }
    }

    public void handleStuff() {
        try {
            while (!Thread.interrupted()) {
                while (isBusy) {
                    work();
                    waitForStuff();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while handling stuff");
        }

    }

    @Override
    public void run() {
        handleStuff();
    }
}

class Listener1 implements Runnable {
    protected Worker1 busy;
    protected String name;

    public Listener1(Worker1 busy, String name) {
        this.busy = busy;
        this.name = name;
    }

    public void monitoring() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(name + " Monitoring " + busy.name + " `s state " + busy.isBusy());
                Thread.sleep(100);
                if (!this.busy.isBusy()) {
                    System.out.println(name + " Keep " + busy.name + "`s hand busy");
                    busy.setBusy(true);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while monitoring");
        }

    }

    @Override
    public void run() {
        monitoring();
    }
}

class Listener2 implements Runnable {
    protected Worker2 busy;
    protected String name;

    public Listener2(Worker2 busy, String name) {
        this.busy = busy;
        this.name = name;
    }

    public void monitoring() {
        try {
            while (!Thread.interrupted()) {
                while (!busy.isBusy) {
                    busy.assignJob(this);
                    busy.waitForDone();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while monitoring");
        }

    }

    @Override
    public void run() {
        monitoring();
    }
}