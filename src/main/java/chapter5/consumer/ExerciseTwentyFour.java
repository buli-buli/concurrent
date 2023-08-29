package chapter5.consumer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME ExerciseTwentyFour
 * @Description 使用wait()和notify()解决单个生产者、单个消费者问题
 * @Author yu
 * @Date 2023/8/28 22:13
 * @Version 1.0
 */
public class ExerciseTwentyFour {
    ExecutorService exec = Executors.newCachedThreadPool();
    Queue<Item> storage = new LinkedList<>();
    Producer producer = new Producer(this);
    Consumer consumer = new Consumer(this);

    public ExerciseTwentyFour() {
        exec.execute(producer);
        exec.execute(consumer);
    }

    public static void main(String[] args) {
        new ExerciseTwentyFour();
    }
}

class Item {
    private final int itemNum;

    public Item(int itemNum) {
        this.itemNum = itemNum;
    }

    @Override
    public String toString() {
        return "Item{" + "itemNum=" + itemNum + '}';
    }
}

class Producer implements Runnable {
    private int count = 0;
    ExerciseTwentyFour market;

    public Producer(ExerciseTwentyFour market) {
        this.market = market;
    }

    protected int getCount() {
        return count;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                while (count < 100) {
                    Item item = new Item(++count);
                    if (market.storage.offer(item)) {
                        System.out.println("Produced " + item);
                        synchronized (market.consumer) {
                            market.consumer.notifyAll();
                        }
                    }
                    // Storage has only 10 Items
                    synchronized (this) {
                        while (!(market.storage.size() < 10)) {
                            wait();
                        }
                    }
                }
                market.exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Producer interrupted");
            System.out.println("Produced " + count + " Items");
        }
    }
}

class Consumer implements Runnable {
    int consumed = 0;
    ExerciseTwentyFour market;
    List<Item> cart = new ArrayList<>();

    Consumer(ExerciseTwentyFour m) {
        market = m;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (!(cart.size() < market.producer.getCount())) {
                        wait();
                    }
                }

                if (cart.add(market.storage.poll())) {
                    System.out.println("Consuming Item " + ++consumed);

                    synchronized (market.producer) {
                        market.producer.notifyAll();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Consumer interrupted");
            System.out.println("Consumed " + consumed + " Items");
        }
    }
}