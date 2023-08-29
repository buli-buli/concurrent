package chapter7;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.*;
import java.util.concurrent.*;

/**
 * @ClassNAME ExerciseThirtyThree
 * @Description 修改GreenHouseScheduler，使其使用DelayQueue来替代ScheduledExecutor
 * @Author yu
 * @Date 2023/8/30 0:00
 * @Version 1.0
 */
abstract class GreenHouseTask implements Runnable, Delayed {
    protected long delayTime;
    private long trigger;

    public GreenHouseTask() {
        delayTime = 0;
        this.trigger = System.nanoTime();
    }

    public GreenHouseTask(long d) {
        delayTime = d;
        this.trigger = System.nanoTime() + NANOSECONDS.convert(delayTime, MILLISECONDS);
    }

    public long getDelay(TimeUnit unit) {
        return unit.convert(trigger - System.nanoTime(), NANOSECONDS);
    }

    public int compareTo(Delayed arg) {
        GreenHouseTask that = (GreenHouseTask)arg;
        if (trigger < that.trigger) {
            return -1;
        }
        if (trigger > that.trigger) {
            return 1;
        }
        return 0;
    }

    abstract public GreenHouseTask create(long d);

    abstract public void run();
}

class GreenHouseController {
    private volatile boolean light = false;
    private volatile boolean water = false;
    private String thermostat = "Day";
    static DelayQueue<GreenHouseTask> tasks = new DelayQueue<>();

    public synchronized String getThermostat() {
        return thermostat;
    }

    public synchronized void setThermostat(String thermostat) {
        this.thermostat = thermostat;
    }

    class LightOn33 extends GreenHouseTask {
        public LightOn33() {
            super();
        }

        public LightOn33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new LightOn33(d);
        }

        @Override
        public void run() {
            System.out.println("Turning on lights");
            light = true;
        }
    }

    class LightOff33 extends GreenHouseTask {
        public LightOff33() {}

        public LightOff33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new LightOn33(d);
        }

        @Override
        public void run() {
            System.out.println("Turning off lights");
            light = false;
        }
    }

    class WaterOn33 extends GreenHouseTask {
        public WaterOn33() {
            super();
        }

        public WaterOn33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new WaterOn33(d);
        }

        @Override
        public void run() {
            System.out.println("Truning greenhouse water on");
            water = true;
        }
    }

    class WaterOff33 extends GreenHouseTask {
        public WaterOff33() {}

        public WaterOff33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new WaterOff33(d);
        }

        @Override
        public void run() {
            System.out.println("Turning greenhouse water off");
        }
    }

    class ThermostatNight33 extends GreenHouseTask {
        public ThermostatNight33() {}

        public ThermostatNight33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new ThermostatNight33(d);
        }

        @Override
        public void run() {
            System.out.println("Thermostat to night setting");
            setThermostat("Night");
        }
    }

    class ThermostatDay33 extends GreenHouseTask {

        public ThermostatDay33() {}

        public ThermostatDay33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new ThermostatNight33(d);
        }

        @Override
        public void run() {
            System.out.println("Thermostat to day setting");
            setThermostat("Day");
        }
    }

    class Bell33 extends GreenHouseTask {
        public Bell33() {}

        public Bell33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new Bell33(d);
        }

        @Override
        public void run() {
            System.out.println("Bing!");
        }
    }

    class StopController extends GreenHouseTask {
        private ExecutorService exec;

        public StopController(long d, ExecutorService exec) {
            super(d);
            this.exec = exec;
        }

        @Override
        public GreenHouseTask create(long d) {
            return new StopController(d, Executors.newCachedThreadPool());
        }

        @Override
        public void run() {
            System.out.println("Terminating");
            exec.shutdownNow();

            new Thread() {
                public void run() {
                    for (DataPoint33 d : data) {
                        System.out.println(d);
                    }
                }
            }.start();
        }
    }

    public static class GreenHouseGo implements Runnable {
        private DelayQueue<GreenHouseTask> q;

        public GreenHouseGo(DelayQueue<GreenHouseTask> q) {
            this.q = q;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    q.take().run();
                }
            } catch (InterruptedException e) {

            }
            System.out.println("Finished GreenHouseGo");
        }
    }

    static class DataPoint33 {
        final Calendar time;
        final float temperature;
        final float humidity;

        public DataPoint33(Calendar time, float temperature, float humidity) {
            this.time = time;
            this.temperature = temperature;
            this.humidity = humidity;
        }

        @Override
        public String toString() {
            return time.getTime() + String.format(" temperature: %1$.1f humidity: %2$.2f", temperature, humidity);
        }
    }

    private Calendar lastTime = Calendar.getInstance();
    {
        lastTime.set(Calendar.MINUTE, 30);
        lastTime.set(Calendar.SECOND, 00);
    }
    private float lastTemp = 65.0f;
    private int tempDirection = +1;
    private float lastHumidity = 50.0f;
    private int humidityDirection = +1;
    private Random rand = new Random(47);
    List<DataPoint33> data = Collections.synchronizedList(new ArrayList<>());

    class CollectData33 extends GreenHouseTask {

        public CollectData33() {}

        public CollectData33(long d) {
            super(d);
        }

        @Override
        public GreenHouseTask create(long d) {
            return new CollectData33(d);
        }

        @Override
        public void run() {
            System.out.println("Collecting data");

            synchronized (this) {
                lastTime.set(Calendar.MINUTE, lastTime.get(Calendar.MINUTE) + 30);

                if (rand.nextInt(5) == 4) {
                    tempDirection = -tempDirection;
                }
                lastTemp = lastTemp + tempDirection * (1.0f + rand.nextFloat());

                if (rand.nextInt() == 4) {
                    humidityDirection = -humidityDirection;
                }
                lastHumidity = lastHumidity + humidityDirection * rand.nextFloat();

                data.add(new DataPoint33((Calendar)lastTime.clone(), lastTemp, lastHumidity));
            }
        }
    }
}

public class ExerciseThirtyThree {
    public static void repeat(GreenHouseController controller, GreenHouseTask task, long interval, long duration)
        throws Exception {
        if (interval <= duration) {
            for (int i = 0; i < duration / interval; i++) {
                GreenHouseTask t = task.create(interval * (i + 1));
                controller.tasks.put(t);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        GreenHouseController ghc = new GreenHouseController();

        repeat(ghc, ghc.new Bell33(), 1000, 4000);
        repeat(ghc, ghc.new ThermostatNight33(), 2000, 4000);
        repeat(ghc, ghc.new LightOn33(), 200, 4000);
        repeat(ghc, ghc.new LightOff33(), 400, 4000);
        repeat(ghc, ghc.new WaterOn33(), 600, 4000);
        repeat(ghc, ghc.new WaterOff33(), 800, 4000);
        repeat(ghc, ghc.new ThermostatDay33(), 1400, 4000);
        repeat(ghc, ghc.new CollectData33(), 500, 4000);
        ghc.tasks.put(ghc.new StopController(5000, exec));
        exec.execute(new GreenHouseController.GreenHouseGo(ghc.tasks));
    }
}
