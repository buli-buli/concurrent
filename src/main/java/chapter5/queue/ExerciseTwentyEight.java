package chapter5.queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

import chapter2.LiftOff;

/**
 * @ClassNAME ExerciseTwentyEight
 * @Description 练习 28 添加一个将LiftOff放置到BlockingQueue中的任务，而不要放置在main()中
 * @Author yu
 * @Date 2023/8/29 14:06
 * @Version 1.0
 */
public class ExerciseTwentyEight {
    static void getkey() {
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static void getKey(String msg) {
        System.out.println(msg);
        getkey();
    }

    static void test(String msg, BlockingQueue<LiftOff> queue) {
        System.out.println(msg);
        LiftOffRunner runner = new LiftOffRunner(queue);
        LiftOffAdder adder = new LiftOffAdder(runner);

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(runner);
        exec.execute(adder);

        getKey("Press 'Enter' (" + msg + ")");
        System.out.println("Finished " + msg + " test");
        exec.shutdownNow();
    }

    public static void main(String[] args) {
        test("LinkedBlockingQueue", new LinkedBlockingQueue<>());
        test("ArrayBlockingQueue", new ArrayBlockingQueue<>(3));
        test("SynchronousQueue", new SynchronousQueue<>());
    }
}

class LiftOffAdder implements Runnable {
    private LiftOffRunner runner;

    public LiftOffAdder(LiftOffRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            runner.add(new LiftOff(3));
        }
    }
}
