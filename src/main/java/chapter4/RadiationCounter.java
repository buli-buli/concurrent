package chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RadiationCounter {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        int numSensors = 10; // 指定传感器数量

        for (int i = 0; i < numSensors; i++) {
            exec.execute(new Sensor(i));
        }

        TimeUnit.SECONDS.sleep(3);

        Sensor.cancel();

        exec.shutdown();

        if (!exec.awaitTermination(250, TimeUnit.MILLISECONDS)) {
            System.out.println("Some tasks were not terminated");
        }

        System.out.println("Total Radiation Count: " + Sensor.getTotalCount());
        System.out.println("Sum of Sensor Counts: " + Sensor.sumSensorCounts());
    }
}

class RadiationCount {
    private int count = 0;
    private Random rand = new Random(47);

    public synchronized int increment() {
        int temp = count;
        if (rand.nextBoolean()) {
            Thread.yield();
        }

        return (count = ++temp);
    }

    public synchronized int value() {
        return count;
    }
}

class Sensor implements Runnable {
    private static RadiationCount radiationCount = new RadiationCount();
    private static List<Sensor> sensors = new ArrayList<>();
    private int count = 0;
    private final int id;
    private static volatile boolean canceled = false;

    public static void cancel() {
        canceled = true;
    }

    public Sensor(int id) {
        this.id = id;
        sensors.add(this);
    }

    @Override
    public void run() {
        while (!canceled) {
            synchronized (this) {
                count++;
            }
            System.out.println(this + " Radiation Count: " + radiationCount.increment());

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted");
            }
        }

        System.out.println("Stopping " + this);
    }

    public synchronized int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Sensor " + id + ": Count=" + getCount();
    }

    public static int getTotalCount() {
        return radiationCount.value();
    }

    public static int sumSensorCounts() {
        int sum = 0;
        for (Sensor sensor : sensors) {
            sum += sensor.getCount();
        }

        return sum;
    }
}
