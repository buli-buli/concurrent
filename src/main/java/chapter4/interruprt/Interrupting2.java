package chapter4.interruprt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassNAME Interrupting2
 * @Description 在ReentrantLock上阻塞的任务具备可以被中断的能力
 * @Author yu
 * @Date 2023/8/27 20:58
 * @Version 1.0
 */
public class Interrupting2 {
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new Blocked2());
        t.start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Issuing t.interrupt()");
        t.interrupt();
    }
}

class BlockedMutex {
    private Lock lock = new ReentrantLock();

    public BlockedMutex() {
        lock.lock();
    }

    public void f() {
        try {
            lock.lockInterruptibly();
            System.out.println("lock acquired in f()");
        } catch (InterruptedException e) {
            System.out.println("Interrupted from lock acquisition in f()");
        }
    }
}

class Blocked2 implements Runnable {
    BlockedMutex blocked = new BlockedMutex();

    @Override
    public void run() {
        System.out.println("Waiting for f() in BlockedMutex");
        blocked.f();
        System.out.println("Broken out of blocked call");
    }
}