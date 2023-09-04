package chapter9;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassNAME FastSimulation
 * @Description 假想的某种类型的遗传算法仿真，使用Atomic对象而不是Locj对象来防止互斥开销
 * @Author yu
 * @Date 2023/9/4 16:35
 * @Version 1.0
 */
public class FastSimulation {
    static final int N_ELEMENT = 100000;
    static final int N_GENS = 30;
    static final int N_EVOLVERS = 50;
    static final AtomicInteger[][] GRID = new AtomicInteger[N_ELEMENT][N_GENS];
    static Random rand = new Random(47);

    static class Evolver implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                int element = rand.nextInt(N_ELEMENT);
                for (int i = 0; i < N_GENS; i++) {
                    int previous = element - 1;
                    if (previous < 0) {
                        previous = N_ELEMENT - 1;
                    }
                    int next = element + 1;
                    if (next >= N_ELEMENT) {
                        next = 0;
                    }

                    int oldValue = GRID[element][i].get();
                    int newValue = oldValue + GRID[previous][i].get() + GRID[next][i].get();
                    newValue /= 3;
                    if (!GRID[element][i].compareAndSet(oldValue, newValue)) {
                        System.out.println("Old value changed failed from " + oldValue);
                    } else {
                        System.out.println("Old value changed from " + oldValue + " to " + newValue);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < N_ELEMENT; i++) {
            for (int j = 0; j < N_GENS; j++) {
                GRID[i][j] = new AtomicInteger(rand.nextInt(1000));
            }
        }

        for (int i = 0; i < N_EVOLVERS; i++) {
            exec.execute(new Evolver());
        }

        TimeUnit.SECONDS.sleep(5);

        exec.shutdownNow();
    }
}
