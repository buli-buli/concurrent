package chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseNineteen
 * @Description 修改OrnamentalGarden，使其使用interrupt()而不是canceled
 * @Author yu
 * @Date 2023/8/26 23:50
 * @Version 1.0
 */
public class ExerciseNineteen {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            new EntranceForNineteen(i).start();
        }

        TimeUnit.SECONDS.sleep(1);

        System.out.println("Total: " + EntranceForNineteen.getTotalCount());
        System.out.println("Sum of Entrances " + EntranceForNineteen.sumEntrances());

        // 中断并清除所有entrance
        EntranceForNineteen.interruptAll();
    }
}

class EntranceForNineteen extends Thread {
    private static Count count = new Count();
    private static List<EntranceForNineteen> entrances = new ArrayList<>();
    private int number = 0;
    private final int id;

    public EntranceForNineteen(int id) {
        this.id = id;
        entrances.add(this);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this) {
                    ++number;
                }
                System.out.println(this + " Total: " + count.increment());

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }

        System.out.println("Stopping " + this);
    }

    public synchronized int getValue() {
        return number;
    }

    @Override
    public String toString() {
        return "Entrance " + id + ": " + getValue();
    }

    public static int getTotalCount() {
        return count.value();
    }

    public static int sumEntrances() {
        int sum = 0;
        for (EntranceForNineteen entrance : entrances) {
            sum += entrance.getValue();
        }

        return sum;
    }

    public static void interruptAll() {
        for (EntranceForNineteen entrance : entrances) {
            entrance.interrupt();
        }

        entrances.clear();
    }
}
