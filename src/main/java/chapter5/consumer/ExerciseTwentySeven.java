package chapter5.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassNAME Restaurant1
 * @Description 使用condition.await()与condition.signal()和condition.sinalAll() 代替wait()与notify()和notifyAll()实现饭店建模
 * @Author yu
 * @Date 2023/8/28 21:44
 * @Version 1.0
 */
public class ExerciseTwentySeven {
    Meal meal;
    ExecutorService exec = Executors.newCachedThreadPool();
    WaitPerson1 waitPerson = new WaitPerson1(this);
    Chef1 chef = new Chef1(this);

    public ExerciseTwentySeven() {
        exec.execute(chef);
        exec.execute(waitPerson);
    }

    public static void main(String[] args) {
        new ExerciseTwentySeven();
    }
}

class Meal1 {
    private final int orderNum;

    public Meal1(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "Meal{" + "orderNum=" + orderNum + '}';
    }
}

class WaitPerson1 implements Runnable {
    protected Lock lock = new ReentrantLock();
    protected Condition condition = lock.newCondition();
    private ExerciseTwentySeven restaurant;

    public WaitPerson1(ExerciseTwentySeven restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                lock.lock();
                try {
                    while (restaurant.meal == null) {
                        condition.await();
                    }
                } finally {
                    lock.unlock();
                }

                System.out.println("WaitPerson got " + restaurant.meal);

                restaurant.chef.lock.lock();
                try {
                    restaurant.meal = null;
                    restaurant.chef.condition.signalAll();
                } finally {
                    restaurant.chef.lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("WaitPerson interrupted");
        }
    }
}

class Chef1 implements Runnable {
    protected Lock lock = new ReentrantLock();
    protected Condition condition = lock.newCondition();
    private ExerciseTwentySeven restaurant;
    private int count = 0;

    public Chef1(ExerciseTwentySeven restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                lock.lock();
                try {
                    while (restaurant.meal != null) {
                        condition.await();
                    }
                } finally {
                    lock.unlock();
                }

                if (++count == 10) {
                    System.out.println("Out of food, closing");
                    restaurant.exec.shutdownNow();
                    return;
                }

                System.out.println("Order up! ");

                restaurant.waitPerson.lock.lock();
                try {
                    restaurant.meal = new Meal(count);
                    restaurant.waitPerson.condition.signalAll();
                } finally {
                    restaurant.waitPerson.lock.unlock();
                }

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Chef interrupted");
        }
    }
}
