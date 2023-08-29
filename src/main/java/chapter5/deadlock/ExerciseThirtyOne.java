package chapter5.deadlock;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseThirtyOne
 * @Description 练习 31 修改DeadlockingDiningPhilosophers，使得当哲学家用完筷子之后，把筷子放在一个筷笼里。
 *              当哲学家要就餐的时候，他们就从筷笼里取出下两根可用的筷子。你能仅仅通过减少可用的筷子数目就重新引入死锁吗？
 * 
 *              在筷笼里加了一个同步方法，每次直接拿一双筷子，而不是一根一根拿。并且只在筷笼里有两只以上的筷子时才去除筷子。
 *              破坏了形成死锁的四个条件中的条件2.至少有一个任务它必须持有一个资源且正在等待获取一个被当前别的任务持有的资源。
 *              不能通过减少筷子的数量重新引入死锁，因为无论筷子的数量怎么变化，条件二都被破坏了。如果把筷子的数量减少为 1的话 所有哲学家进程都会阻塞，但那不是死锁，而是由于资源缺乏导致进程饿死。
 * @Author yu
 * @Date 2023/8/29 16:39
 * @Version 1.0
 */
public class ExerciseThirtyOne {
    public static void main(String[] args) throws Exception {
        test(5, 5, false);
    }

    static void test(int ponder, int size, boolean isTimeOut) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        LinkedList<Chopstick> sticks = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            sticks.add(new Chopstick());
        }
        ChopstickBin bin = new ChopstickBin(sticks);

        for (int i = 0; i < size; i++) {
            exec.execute(new Philosopher31(bin, i, ponder));
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

class Philosopher31 implements Runnable {
    private Chopstick left;
    private Chopstick right;
    private ChopstickBin bin;
    private final int id;
    private final int ponderFactor;
    private Random rand = new Random(47);

    public void setLeft(Chopstick left) {
        this.left = left;
    }

    public void setRight(Chopstick right) {
        this.right = right;
    }

    private void pause() throws InterruptedException {
        if (ponderFactor == 0) {
            return;
        }
        TimeUnit.MILLISECONDS.sleep(rand.nextInt(ponderFactor * 250));
    }

    public Philosopher31(ChopstickBin bin, int id, int ponderFactor) {
        this.bin = bin;
        this.id = id;
        this.ponderFactor = ponderFactor;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(this + " " + "thinking");
                pause();
                System.out.println(this + " " + "grabbing both");
                bin.getChopSticks(this);

                System.out.println(this + " " + "eating");
                bin.putChopSticks(left, right);
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

class ChopstickBin {
    private LinkedList<Chopstick> queue = new LinkedList<>();

    public ChopstickBin(LinkedList<Chopstick> queue) {
        this.queue = queue;
    }

    protected synchronized void getChopSticks(Philosopher31 philosopher) throws InterruptedException {
        while (queue.size() < 2) {
            wait();
        }

        philosopher.setLeft(queue.poll());
        philosopher.setRight(queue.poll());
        notifyAll();
    }

    protected synchronized void putChopSticks(Chopstick left, Chopstick right) {
        queue.add(left);
        queue.add(right);
    }
}
