package chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseTwenty
 * @Description 练习20
 * @Author yu 修改CachedThreadPool，使所有任务在结束前都将收到一个interrupt()
 * @Date 2023/8/27 20:15
 * @Version 1.0
 */
public class ExerciseTwenty {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Future> futureList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futureList.add(exec.submit(new LiftOff()));
        }

        TimeUnit.SECONDS.sleep(3);

        for (Future f : futureList) {
            f.cancel(true);
        }

        exec.shutdown();
    }
}

class LiftOff implements Runnable {
    protected int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public LiftOff() {}

    public LiftOff(int countDown) {
        this.countDown = countDown;
    }

    public String status() {
        return "#" + id + "(" + (countDown > 0 ? countDown : "LiftOff!") + ").";
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && countDown-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            System.out.println(status());
            Thread.yield();
        }
    }
}
