package chapter3;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME ExerciseFifteen
 * @Description 21章练习 15 创建一个类。它具有三个方法，这些方法包含一个临界区，所有对该临界区的同步都是在一个对象上的。 创建多个任务来掩饰这些方法同时只能运行一个。
 *              然后修改这些方法，使得每个方法都在不同的对象上同步，并展示所有三个方法可以同时运行。
 * 
 *              synchronized块必须给定一个在其上进行同步的对象，并且最合理的方式是，使用其方法正在被调用的当前对象synchronized(this)
 * @Author yu
 * @Date 2023/8/25 17:21
 * @Version 1.0
 */
public class ExerciseFifteen {
    public static void main(String[] args) {
        ExerciseFifteen fifteen = new ExerciseFifteen();
        // fifteen.testBySingleObject();
        fifteen.testByMultiObject();
    }

    // 测试多个任务同时访问一个对象即多个任务在同一个对象上同步，演示这些方法同时只能运行一个
    public void testBySingleObject() {
        Random r = new Random();
        Counter c = new Counter();
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            exec.execute(new AddTask(r, c));
        }

        exec.shutdown();
    }

    // 每个方法都在不同的对象上同步所有三个方法可以同时运行
    public void testByMultiObject() {
        Random r = new Random();
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            exec.execute(new AddTask(r, new Counter()));
        }

        exec.shutdown();
    }
}

class Counter {
    private Integer counter = new Integer(0);

    public void addOne() {
        synchronized (this) {
            counter += 1;
            System.out.println("after add one :" + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            counter -= 1;
            System.out.println("after minus one: " + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }
    }

    public void addTwo() {
        synchronized (this) {
            counter += 2;
            System.out.println("after add two: " + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            counter -= 2;
            System.out.println("after minus two: " + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }
    }

    public void addThree() {
        synchronized (this) {
            counter += 3;
            System.out.println("after add three: " + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

            counter -= 3;
            System.out.println("after minus three: " + counter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }
    }
}

class AddTask implements Runnable {
    private Random r;
    private Counter c;

    public AddTask(Random r, Counter c) {
        this.r = r;
        this.c = c;
    }

    @Override
    // 每次生成一个1-3范围类的整数，根据生成的整数判断调用哪个方法
    public void run() {
        int num = r.nextInt(3) + 1;
        switch (num) {
            case 1:
                c.addOne();
                break;
            case 2:
                c.addTwo();
                break;
            case 3:
                c.addThree();
                break;
            default:
                System.out.println("无操作");
        }
    }
}