package chapter8.car;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @ClassNAME CarBuilder
 * @Description 假想的用于汽车的机器人组装线
 * @Author yu
 * @Date 2023/8/31 15:54
 * @Version 1.0
 */
public class CarBuilder {
    public static void main(String[] args) throws Exception {
        CarQueue chasisQueue = new CarQueue(), finishingQueue = new CarQueue();

        ExecutorService exec = Executors.newCachedThreadPool();
        RobotPool robotPool = new RobotPool();
        exec.execute(new EngineRobot(robotPool));
        exec.execute(new DriveTrainRobot(robotPool));
        exec.execute(new WheelRobot(robotPool));
        exec.execute(new Assembler(chasisQueue, finishingQueue, robotPool));
        exec.execute(new Reporter(finishingQueue));
        exec.execute(new ChassisBuilder(chasisQueue));

        TimeUnit.SECONDS.sleep(7);

        exec.shutdownNow();
    }
}

class Car {
    private final int id;
    private boolean egine = false, driveTrain = false, wheels = false;

    public Car(int id) {
        this.id = id;
    }

    public Car() {
        id = -1;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized void addEngine() {
        egine = true;
    }

    public synchronized void addDriveTrain() {
        driveTrain = true;
    }

    public synchronized void addWheels() {
        wheels = true;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", egine=" + egine + ", driveTrain=" + driveTrain + ", wheels=" + wheels + '}';
    }
}

class CarQueue extends LinkedBlockingQueue<Car> {}

class ChassisBuilder implements Runnable {
    private CarQueue carQueue;
    private int counter = 0;

    public ChassisBuilder(CarQueue carQueue) {
        this.carQueue = carQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(500);

                Car c = new Car(counter++);

                System.out.println("ChassisBuilder created " + c);

                carQueue.put(c);
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted: ChasisBuilder");
        }

        System.out.println("ChasisBuilder off");
    }
}

class Assembler implements Runnable {
    private CarQueue chassisQueue, finishingQueue;
    private Car car;
    private CyclicBarrier barrier = new CyclicBarrier(4);
    private RobotPool robotPool;

    public Assembler(CarQueue chassisQueue, CarQueue finishingQueue, RobotPool robotPool) {
        this.chassisQueue = chassisQueue;
        this.finishingQueue = finishingQueue;
        this.robotPool = robotPool;
    }

    public Car car() {
        return car;
    }

    public CyclicBarrier barrier() {
        return barrier;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                car = chassisQueue.take();
                robotPool.hire(EngineRobot.class, this);
                robotPool.hire(DriveTrainRobot.class, this);
                robotPool.hire(WheelRobot.class, this);

                barrier.await();
                finishingQueue.put(car);
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting Assmbler via interrupt");
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Assembler off");
    }
}

class Reporter implements Runnable {
    private CarQueue carQueue;

    public Reporter(CarQueue carQueue) {
        this.carQueue = carQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(carQueue.take());
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting Reporter via interrupt");
        }

        System.out.println("Reporter off");
    }
}

abstract class Robot implements Runnable {
    private RobotPool pool;

    public Robot(RobotPool robotPool) {
        this.pool = robotPool;
    }

    protected Assembler assembler;

    public Robot assignAssembler(Assembler assembler) {
        this.assembler = assembler;
        return this;
    }

    private boolean engage = false;

    public synchronized void engage() {
        engage = true;
        notifyAll();
    }

    abstract protected void performService();

    @Override
    public void run() {
        try {
            powerDown();// Wait until needed
            while (!Thread.interrupted()) {
                performService();
                assembler.barrier().await();
                powerDown();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting " + this + " via interrupt");
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        System.out.println(this + " off");
    }

    private synchronized void powerDown() throws InterruptedException {
        engage = false;
        assembler = null;
        pool.release(this);
        while (engage == false) {
            wait();
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}

class EngineRobot extends Robot {
    public EngineRobot(RobotPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " installing engine");
        assembler.car().addEngine();
    }
}

class DriveTrainRobot extends Robot {
    public DriveTrainRobot(RobotPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " installing drivetrain");
        assembler.car().addDriveTrain();
    }
}

class WheelRobot extends Robot {
    public WheelRobot(RobotPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " installing wheel");
        assembler.car().addWheels();
    }
}

class RobotPool {
    private Set<Robot> pool = new HashSet<>();

    public synchronized void add(Robot r) {
        pool.add(r);
        notifyAll();
    }

    public synchronized void hire(Class<? extends Robot> robotType, Assembler d) throws InterruptedException {
        for (Robot r : pool) {
            if (r.getClass().equals(robotType)) {
                pool.remove(r);
                r.assignAssembler(d);
                r.engage();
                return;
            }
        }

        wait();
        hire(robotType, d);
    }

    public synchronized void release(Robot r) {
        add(r);
    }
}