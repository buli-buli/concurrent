package chapter2;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseSix
 * @Description 21.2.5 练习6
 * @Author yu
 * @Date 2023/8/23 17:52
 * @Version 1.0
 */
public class ExerciseSix {
    public static void main(String[] args) {
        Random r = new Random();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            exec.execute(new SleepTask(r));
        }

        exec.shutdown();
    }
}

class SleepTask implements Runnable {
    private Random random;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public SleepTask(Random random) {
        this.random = random;
    }

    @Override
    public void run() {
        int times = random.nextInt(10) + 1;

        try {
            TimeUnit.SECONDS.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("#" + id + " 休眠了 " + times + " 秒");
    }
}
