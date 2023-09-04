package chapter9;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassNAME ActiveObjectDemo
 * @Description 活动对象示例
 * @Author yu
 * @Date 2023/9/4 18:18
 * @Version 1.0
 */
public class ActiveObjectDemo {
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private Random rand = new Random(47);

    private void pause(int factor) {
        try {
            TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(factor));
        } catch (InterruptedException e) {
            System.out.println("sleep() interrupted");
        }
    }

    public Future<Integer> calculateInt(final int x, final int y) {
        return exec.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("starting " + x + " + " + y);
                pause(500);
                return x + y;
            }
        });
    }

    public Future<Float> calcuateFloat(final float x, final float y) {
        return exec.submit(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                System.out.println("starting " + x + " + " + y);
                pause(2000);
                return x + y;
            }
        });
    }

    public void shutdown() {
        exec.shutdown();
    }

    public static void main(String[] args) {
        ActiveObjectDemo d1 = new ActiveObjectDemo();
        List<Future<?>> results = new CopyOnWriteArrayList<>();
        for (float f = 0.0f; f < 1.0f; f += 0.2f) {
            results.add(d1.calcuateFloat(f, f));
        }
        for (int i = 0; i < 5; i++) {
            results.add(d1.calculateInt(i, i));
        }

        System.out.println("All asynch calls made");

        while (results.size() > 0) {
            for (Future f : results) {
                if (f.isDone()) {
                    try {
                        System.out.print(f.get() + " ");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    results.remove(f);
                }
            }
        }

        System.out.println();
        d1.shutdown();
    }
}
