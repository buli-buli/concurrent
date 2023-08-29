package chapter6;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME DeadlockingDiningPhilosophers
 * @Description 死锁问题经典示例：哲学家就餐问题
 * 
 *              死锁发生的四个条件
 *
 *              1、互斥条件。任务使用的资源中至少有一个是不能共享的。这里，一根Chopstick一次就只能被一个Philosopher使用
 * 
 *              2、至少有一个任务它必须持有一个资源且正在等待获取一个被当前别的任务持有的资源。 也就是说，要发生死锁，Philosopher必须拿着一根筷子且等待另一根
 * 
 *              3、资源不能被任务抢占，任务必须把资源释放当作普通时间。Philosopher很有礼貌 ，他们不会从其他Philosopher哪里抢Chopstick
 * 
 *              4、必须有循环等待，这时，一个任务等待其他任务所持有的资源，后者又在等待另一个任务所持有的资源，这样一直下去
 *              ，直到有一个任务在等待第一个任务所持有的资源，后者又在等待另一个任务所持有的资源。在DeadlockingDiningPhilosophers
 *              中，因为每个Philosopher都试图先得到右边的Chopstick，然后得到左边的Chopstick，所以发生了循环等待
 * 
 *              要发生死锁的话，以上四个条件都必须满足。所以要防止死锁的话，只需破坏其中一个条件即可。
 * @Author yu
 * @Date 2023/8/29 15:55
 * @Version 1.0
 */
public class DeadlockingDiningPhilosophers {
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
            exec.execute(new Philosopher(sticks[i], sticks[(i + 1) % size], i, ponder));
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

class Chopstick {
    private boolean taken = false;

    public synchronized void take() throws InterruptedException {
        while (taken) {
            wait();
        }
        taken = true;
    }

    public synchronized void drop() {
        taken = false;
        notifyAll();
    }
}

class Philosopher implements Runnable {
    private Chopstick left;
    private Chopstick right;
    private final int id;
    private final int ponderFactor;
    private Random rand = new Random(47);

    private void pause() throws InterruptedException {
        if (ponderFactor == 0) {
            return;
        }
        TimeUnit.MILLISECONDS.sleep(rand.nextInt(ponderFactor * 250));
    }

    public Philosopher(Chopstick left, Chopstick right, int id, int ponderFactor) {
        this.left = left;
        this.right = right;
        this.id = id;
        this.ponderFactor = ponderFactor;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(this + " " + "thinking");
                pause();
                System.out.println(this + " " + "grabbing right");
                right.take();
                System.out.println(this + " " + "grabbing left");
                left.take();
                System.out.println(this + " " + "eating");
                pause();
                right.drop();
                left.drop();
            }
        } catch (InterruptedException e) {
            System.out.println(this + " " + "exiting via interrupt");
        }
    }

    @Override
    public String toString() {
        return "Philosopher{" + "id=" + id + '}';
    }
}
