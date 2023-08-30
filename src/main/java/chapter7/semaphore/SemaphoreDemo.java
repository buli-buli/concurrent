package chapter7.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME SemaphoreDemo
 * @Description 计数信号量示例 正常的锁（来自concurrent.locks或内奸的synchronized锁）在任何时刻都只允许一个任务
 *              访问一项资源，而计数信号量允许n个任务同时访问这个资源。你还可以将信号量看作是在向外分发使用 资源的“许可证”，尽管实际上没有使用任何许可证对象
 * @Author yu
 * @Date 2023/8/30 14:51
 * @Version 1.0
 */
public class SemaphoreDemo {
    final static int SIZE = 25;

    public static void main(String[] args) throws Exception {
        final Pool<Fat> pool = new Pool<>(Fat.class, SIZE);

        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < SIZE; i++) {
            exec.execute(new CheckoutTask<>(pool));
        }

        System.out.println("All CheckoutTasks created");

        List<Fat> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Fat f = pool.checkOut();
            System.out.printf(i + " main() thread checked out ");
            f.operation();
            list.add(f);
        }

        Future<?> blocked = exec.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    pool.checkOut();
                    Thread.interrupted();
                } catch (InterruptedException e) {
                    System.out.println("checkout() interrupted");
                }
            }
        });

        TimeUnit.SECONDS.sleep(2);

        blocked.cancel(true);

        System.out.println("Checking in objects in " + list);

        for (Fat f : list) {
            pool.checkIn(f);
            TimeUnit.MILLISECONDS.sleep(20);
        }

        for (Fat f : list) {
            pool.checkIn(f);
        }

        exec.shutdown();
    }
}

class CheckoutTask<T> implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private Pool<T> pool;

    public CheckoutTask(Pool<T> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            T item = pool.checkOut();
            System.out.println(this + " checking out " + item);
            TimeUnit.SECONDS.sleep(1);
            System.out.println(this + " checking in " + item);
            pool.checkIn(item);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public String toString() {
        return "CheckoutTask{" + "id=" + id + '}';
    }
}