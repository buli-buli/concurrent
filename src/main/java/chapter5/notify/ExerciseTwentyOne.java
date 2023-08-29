package chapter5.notify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseTwentyOne
 * @Description 第五节 练习 21 创建两个Runnable，其中一个的run()方法启动并调用wait()，而第二个类 应该捕获第一个Runnable对象的引用，其run()方法应该在一定的秒数之后，为第一个任务
 *              调用notifyAll()，从而使第一个任务可以显示一条消息
 * @Author yu
 * @Date 2023/8/28 15:40
 * @Version 1.0
 */
public class ExerciseTwentyOne {
    public static void main(String[] args) throws InterruptedException {
        WaitingGirl girl = new WaitingGirl();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(girl);
        exec.execute(new ChasingTask(girl));

        TimeUnit.SECONDS.sleep(2);
        exec.shutdown();
    }
}

class WaitingGirl implements Runnable {
    private boolean loved = false;

    public synchronized boolean isLoved() {
        return loved;
    }

    public synchronized void moved() {
        System.out.println("The girl has been moved");
        this.loved = true;
        notifyAll();
    }

    public synchronized void waiting() {
        try {
            while (!loved) {
                System.out.println("The girl is waiting for her lover...");
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while waiting");
        }
    }

    @Override
    public void run() {
        waiting();
    }
}

class ChasingTask implements Runnable {
    private WaitingGirl girl;

    public ChasingTask(WaitingGirl girl) {
        this.girl = girl;
    }

    public void chasing() {
        synchronized (girl) {
            try {
                while (!girl.isLoved()) {
                    TimeUnit.SECONDS.sleep(1);
                    girl.moved();
                    System.out.println("The boy has won the girl`s heart");
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException while chasing");
            }

        }
    }

    @Override
    public void run() {
        chasing();
    }
}
