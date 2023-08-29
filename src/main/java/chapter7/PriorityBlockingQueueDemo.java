package chapter7;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME PriorityBlockingQueueDemo
 * @Description PriorityBlockingQueue示例。一个很基础的优先级队列，它具有可阻塞的读取操作
 * @Author yu
 * @Date 2023/8/29 22:13
 * @Version 1.0
 */
public class PriorityBlockingQueueDemo {
    public static void main(String[] args) {
        Random rand = new Random(47);
        ExecutorService exec = Executors.newCachedThreadPool();
        PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();

        exec.execute(new PriorityTaskProducer(queue, exec));
        exec.execute(new PriorityTaskConsumer(queue));
    }
}

class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
    private Random rand = new Random(47);
    private static int counter = 0;
    private final int id = counter++;

    private final int priority;

    protected static List<PrioritizedTask> sequence = new ArrayList<>();

    public PrioritizedTask(int priority) {
        this.priority = priority;
        sequence.add(this);
    }

    @Override
    public int compareTo(PrioritizedTask arg) {
        return priority < arg.priority ? 1 : (priority > arg.priority) ? -1 : 0;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(rand.nextInt(250));
        } catch (InterruptedException e) {

        }

        System.out.println(this);
    }

    public String toString() {
        return String.format("[%1$-3d]", priority);
    }

    public String summary() {
        return "(" + id + ":" + priority + ")";
    }

    public static class EndSentinel extends PrioritizedTask {
        private ExecutorService exec;

        public EndSentinel(ExecutorService exec) {
            super(-1);
            this.exec = exec;
        }

        public void run() {
            int count = 0;
            System.out.println();
            for (PrioritizedTask pt : sequence) {
                System.out.print(pt.summary() + " ");
                if (++count % 5 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println(this + " Calling shutdownNow()");
            exec.shutdownNow();
        }
    }
}

class PriorityTaskProducer implements Runnable {
    private Random rand = new Random(47);
    private Queue<Runnable> queue;
    private ExecutorService exec;

    public PriorityTaskProducer(Queue<Runnable> queue, ExecutorService exec) {
        this.queue = queue;
        this.exec = exec;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            queue.add(new PrioritizedTask(rand.nextInt(10)));
            Thread.yield();
        }

        try {
            for (int i = 0; i < 10; i++) {
                TimeUnit.MILLISECONDS.sleep(250);
                queue.add(new PrioritizedTask(10));
            }

            for (int i = 0; i < 10; i++) {
                queue.add(new PrioritizedTask(i));
            }

            queue.add(new PrioritizedTask.EndSentinel(exec));
        } catch (InterruptedException e) {

        }

        System.out.println("Finished PriorityTaskProducer");
    }
}

class PriorityTaskConsumer implements Runnable {
    private PriorityBlockingQueue<Runnable> q;

    public PriorityTaskConsumer(PriorityBlockingQueue<Runnable> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                q.take().run();
            }
        } catch (InterruptedException e) {

        }

        System.out.println("Finished PriorityTaskConsumer");
    }
}