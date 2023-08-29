package chapter5.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseTwentySix
 * @Description 向Restaurant中添加一个BusBoy类。在上菜之后，WaitPerson应该通知BusBoy清理
 * @Author yu
 * @Date 2023/8/28 22:32
 * @Version 1.0
 */
public class ExerciseTwentySix {
    Meal meal;
    ExecutorService exec = Executors.newCachedThreadPool();
    WaitPerson26 waitPerson = new WaitPerson26(this);
    Chef26 chef = new Chef26(this);
    BusBoy busBoy = new BusBoy(this);

    public ExerciseTwentySix() {
        exec.execute(chef);
        exec.execute(waitPerson);
        exec.execute(busBoy);
    }

    public static void main(String[] args) {
        new ExerciseTwentySix();
    }

}

class BusBoy implements Runnable {
    private ExerciseTwentySix restaurant;

    public BusBoy(ExerciseTwentySix restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.waitPerson.clean) {
                        wait();
                    }
                }

                System.out.println("BusBoy cleaning up " + restaurant.waitPerson.m);
                restaurant.waitPerson.clean = true;
            }
        } catch (InterruptedException e) {
            System.out.println("BusBoy interrupted");
        }
    }
}

class WaitPerson26 implements Runnable {
    private ExerciseTwentySix restaurant;
    protected boolean clean = true;
    protected Meal m;

    public WaitPerson26(ExerciseTwentySix restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.meal == null) {
                        wait();
                    }
                }

                m = restaurant.meal;
                System.out.println("WaitPerson got " + m);

                synchronized (restaurant.chef) {
                    restaurant.meal = null;
                    restaurant.chef.notifyAll();
                }

                System.out.println("WaitPerson delivered " + m);

                synchronized (restaurant.busBoy) {
                    clean = false;
                    restaurant.busBoy.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("WaitPerson interrupted");
        }
    }
}

class Chef26 implements Runnable {
    private ExerciseTwentySix restaurant;
    private int count = 0;

    public Chef26(ExerciseTwentySix restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.meal != null) {
                        wait();
                    }
                }

                if (++count == 10) {
                    System.out.println("Out of food, closing");
                    restaurant.exec.shutdownNow();
                    return;
                }

                System.out.println("Order up! ");

                synchronized (restaurant.waitPerson) {
                    restaurant.meal = new Meal(count);
                    restaurant.waitPerson.notifyAll();
                }

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Chef interrupted");
        }
    }
}
