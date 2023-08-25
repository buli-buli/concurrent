package chapter3.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME ExerciseEleven
 * @Description 21章 练习 11
 * @Author yu
 * @Date 2023/8/24 15:29
 * @Version 1.0
 */
public class ExerciseEleven {

}

class Checker implements Runnable {
    private Operater operater;
    private final int id;

    public Checker(Operater operater, int id) {
        this.operater = operater;
        this.id = id;
    }

    @Override
    public void run() {
        while (!operater.isCanceled()) {
            int sum = operater.operateAndSum();
            if (sum != 100) {
                System.out.println("sum does not equal to 100 !");
                operater.cancel();
            }
        }
    }

    public static void test(Operater o, int count) {
        System.out.println("press Control-C to exit");
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            exec.execute(new Checker(o, i));
        }

        exec.shutdown();
    }

    public static void test(Operater o) {
        test(o, 10);
    }

    public static void main(String[] args) {
        Checker.test(new Operater());
    }
}

class Operater {
    private int a = 50;
    private int b = 50;
    private volatile boolean canceled = false;

    public /*synchronized*/ int operateAndSum() {
        // 方法执行完后a和b的和仍然为100
        a -= 10;

        b += 20;

        a -= 10;

        return a + b;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
