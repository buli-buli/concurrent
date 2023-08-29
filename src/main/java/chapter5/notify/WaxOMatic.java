package chapter5.notify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME WaxOMatic
 * @Description wait()和notify()示例，汽车抛光涂蜡任务 通常情况下，对于等待特定条件的多线程代码，应该使用 while 循环 来检查条件，以确保在条件满足之前线程一直等待，并且要在满足条件时使用
 *              notify() 或 notifyAll() 来 通知等待线程。这可以确保线程在正确的时间唤醒和执行
 * @Author yu
 * @Date 2023/8/28 15:21
 * @Version 1.0
 */
public class WaxOMatic {
    public static void main(String[] args) throws InterruptedException {
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOff(car));
        exec.execute(new WaxOn(car));

        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class Car {
    private boolean waxOn = false;

    public synchronized void waxed() {
        waxOn = true;
        // notify();
        notifyAll();
    }

    public synchronized void buffed() {
        waxOn = false;
        // notify();
        notifyAll();
    }

    public synchronized void waitForWaxing() throws InterruptedException {
        while (waxOn == false) {
            wait();
        }
    }

    public synchronized void waitForBuffing() throws InterruptedException {
        while (waxOn == true) {
            wait();
        }
    }
}

class WaxOn implements Runnable {
    private Car car;

    public WaxOn(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("Wax on! ");
                TimeUnit.MILLISECONDS.sleep(200);
                car.waxed();
                car.waitForBuffing();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupt");
        }
    }
}

class WaxOff implements Runnable {
    private Car car;

    public WaxOff(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitForWaxing();
                System.out.println("Wax Off!");
                TimeUnit.MILLISECONDS.sleep(200);
                car.buffed();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupt");
        }
        System.out.println("Ending Wax Off task");
    }
}
