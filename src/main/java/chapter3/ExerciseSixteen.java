package chapter3;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassNAME ExerciseSixteen
 * @Description 使用显式的Lock对象来修改练习15
 * @Author yu
 * @Date 2023/8/25 17:49
 * @Version 1.0
 */
public class ExerciseSixteen {
    public static void main(String[] args) {
        ExerciseSixteen sixteen = new ExerciseSixteen();
        sixteen.testBySingleObject();
        // sixteen.testByMultiObject();
    }

    public void testBySingleObject() {
        Random r = new Random();
        CounterForSixteen c = new CounterForSixteen();
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            exec.execute(new AddTaskForSixTeen(r, c));
        }

        exec.shutdown();
    }

    public void testByMultiObject() {
        Random r = new Random();
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            exec.execute(new AddTaskForSixTeen(r, new CounterForSixteen()));
        }

        exec.shutdown();
    }
}

class CounterForSixteen {
    private Lock lock = new ReentrantLock();
    private Integer counter = new Integer(0);

    public void addOne() {
        lock.lock();
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
        lock.unlock();
    }

    public void addTwo() {
        lock.lock();
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
        lock.unlock();
    }

    public void addThree() {
        lock.lock();
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
        lock.unlock();
    }
}

class AddTaskForSixTeen implements Runnable {
    private Random r;
    private CounterForSixteen c;

    public AddTaskForSixTeen(Random r, CounterForSixteen c) {
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