package chapter7.exchanger;

import java.util.List;
import java.util.concurrent.*;

import chapter7.semaphore.Fat;
import util.BasicGenerator;
import util.Generator;

/**
 * @ClassNAME ExchangerDemo
 * @Description Exchanger 是在两个任务之间交换对象的栅栏。当这些任务进入栅栏时，他们各自拥有 一个对象，当它们离开时，它们都拥有之前由对象持有的对象。Exchanger的典型应用场景是
 *              ：一个任务在创建对象，这些对象的生产代价很高昂，而另一个任务在消费这些对象。通过这种 方式，可以有更多的对象在被创建的同时被消费。
 * @Author yu
 * @Date 2023/8/30 15:38
 * @Version 1.0
 */
public class ExchangerDemo {
    static int size = 5;
    static int delay = 3;

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        Exchanger<List<Fat>> xc = new Exchanger<>();
        List<Fat> producerList = new CopyOnWriteArrayList<>();
        List<Fat> consumerList = new CopyOnWriteArrayList<>();
        exec.execute(new ExchangerProducer<>(xc, BasicGenerator.create(Fat.class), producerList));
        exec.execute(new ExchangerConsumer<>(xc, consumerList));

        TimeUnit.SECONDS.sleep(delay);
        exec.shutdownNow();
    }

}

class ExchangerProducer<T> implements Runnable {
    private Generator<T> generator;
    private Exchanger<List<T>> exchanger;
    private List<T> holder;

    public ExchangerProducer(Exchanger<List<T>> exchanger, Generator<T> generator, List<T> holder) {
        this.generator = generator;
        this.exchanger = exchanger;
        this.holder = holder;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (int i = 0; i < ExchangerDemo.size; i++) {
                    holder.add(generator.next());
                }
                holder = exchanger.exchange(holder);
            }
        } catch (InterruptedException e) {

        }
    }
}

class ExchangerConsumer<T> implements Runnable {
    private Exchanger<List<T>> exchanger;
    private List<T> holder;
    private volatile T value;

    public ExchangerConsumer(Exchanger<List<T>> exchanger, List<T> holder) {
        this.exchanger = exchanger;
        this.holder = holder;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                holder = exchanger.exchange(holder);

                for (T x : holder) {
                    value = x;
                    holder.remove(x);
                }
            }
        } catch (InterruptedException e) {

        }

        System.out.println("Final value: " + value);
    }
}
