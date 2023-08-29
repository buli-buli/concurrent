package chapter7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseThirtyTwo
 * @Description 修改OrnamentalGarden.java，使用CountDownLatch 完成装饰性花园。并移除不必要的代码
 * @Author yu
 * @Date 2023/8/25 23:22
 * @Version 1.0
 */
public class ExerciseThirtyTwo {

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < 5; i++) {
            exec.execute(new Entrance(i, latch));
        }

        TimeUnit.SECONDS.sleep(3);

        latch.countDown();

        System.out.println("Total: " + Entrance.getTotalCount());
        System.out.println("Sum of Entrances " + Entrance.sumEntrances());

        exec.shutdown();
    }
}

class Count {
    private int count = 0;
    private Random rand = new Random(47);

    public synchronized int increment() {
        int temp = count;
        if (rand.nextBoolean()) {
            Thread.yield();
        }

        return (count = ++temp);
    }

    public synchronized int value() {
        return count;
    }
}

class Entrance implements Runnable {
    private static Count count = new Count();
    private static List<Entrance> entrances = new ArrayList<>();
    private static CountDownLatch latch;
    private int number = 0;
    private final int id;

    public Entrance(int id, CountDownLatch latch) {
        this.latch = latch;
        this.id = id;
        entrances.add(this);
    }

    @Override
    public void run() {
        while (!(latch.getCount() == 0)) {
            synchronized (this) {
                ++number;
            }
            System.out.println(this + " Total: " + count.increment());

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("sleep interrupted");
            }
        }

        System.out.println("Stopping " + this);
    }

    public synchronized int getValue() {
        return number;
    }

    @Override
    public String toString() {
        return "Entrance " + id + ": " + getValue();
    }

    public static int getTotalCount() {
        return count.value();
    }

    public static int sumEntrances() {
        int sum = 0;
        for (Entrance entrance : entrances) {
            sum += entrance.getValue();
        }

        return sum;
    }
}
