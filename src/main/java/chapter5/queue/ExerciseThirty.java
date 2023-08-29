package chapter5.queue;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseThirty
 * @Description TODO
 * @Author yu
 * @Date 2023/8/29 15:35
 * @Version 1.0
 */
public class ExerciseThirty {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        LinkedBlockingQueue<Character> queue = new LinkedBlockingQueue<>();
        exec.execute(new Sender30(queue));
        exec.execute(new Receiver30(queue));

        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class Sender30 implements Runnable {
    private Random rand = new Random(47);
    private LinkedBlockingQueue<Character> queue;

    public Sender30(LinkedBlockingQueue<Character> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (char c = 'A'; c <= 'z'; c++) {
                    queue.put(c);
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(500));
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e + " Sender sleep interrupted");
        }
    }
}

class Receiver30 implements Runnable {
    private LinkedBlockingQueue<Character> queue;

    public Receiver30(LinkedBlockingQueue<Character> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Read: " + queue.take() + ". ");
            }
        } catch (InterruptedException e) {
            System.out.println(e + " Receiver read exception");
        }
    }
}
