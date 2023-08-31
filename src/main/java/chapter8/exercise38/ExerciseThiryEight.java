package chapter8.exercise38;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @ClassNAME ExerciseThiryEight
 * @Description 使用CarBuilder中的方式，对本章中给出的房屋构建过程建模 房屋构建过程在书本703页提到：项目规划：必须先挖房子的地基，但是接下来
 *              可以并行地铺设钢结构和构建水泥部件，而这两项任务必须在混凝土浇筑之前完成。 管道必须在水泥板浇注之前到位，而水泥板必须在开始构筑房屋骨架之前到位。
 * @Author yu
 * @Date 2023/8/31 18:26
 * @Version 1.0
 */
public class ExerciseThiryEight {
    public static void main(String[] args) throws Exception {
        HouseQueue foundationQueue = new HouseQueue(), finishingQueue = new HouseQueue();

        ExecutorService exec = Executors.newCachedThreadPool();
        WorkerPool workerPool = new WorkerPool();
        exec.execute(new PouringWorker(workerPool));
        exec.execute(new SteelWorker(workerPool));
        exec.execute(new CementWorker(workerPool));
        exec.execute(new ConstructionalForce(foundationQueue, finishingQueue, workerPool));
        exec.execute(new Reporter(finishingQueue));
        exec.execute(new FoundationBuilder(foundationQueue));

        TimeUnit.SECONDS.sleep(7);

        exec.shutdownNow();
    }
}

class House {
    private final int id;
    private boolean poured = false // 混凝土浇注
        , structured = false // 铺设钢结构
        , cement = false; // 构建水泥部件

    public House(int id) {
        this.id = id;
    }

    public House() {
        id = -1;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized void pourConcrete() {
        // 混凝土浇注
        poured = true;
    }

    public synchronized void steelStructured() {
        // 铺设钢结构
        structured = true;
    }

    public synchronized void constructCement() {
        // 构建水泥部件
        cement = true;
    }

    @Override
    public String toString() {
        return "House{" + "id=" + id + ", poured=" + poured + ", structured=" + structured + ", cement=" + cement + '}';
    }
}

class HouseQueue extends LinkedBlockingQueue<House> {}

/**
 * @description: 挖地基的task，相当于ChasisBuilder
 * @author: Yzw
 * @date:
 * @param:
 * @return:
 **/
class FoundationBuilder implements Runnable {
    private HouseQueue houseQueue;
    private int counter = 0;

    public FoundationBuilder(HouseQueue houseQueue) {
        this.houseQueue = houseQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(500);

                House h = new House(counter++);

                System.out.println("FoundationBuilder digged foundation " + h);

                houseQueue.put(h);
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted: FoundationBuilder");
        }

        System.out.println("FoundationBuilder off");
    }
}

/**
 * @description: 施工的工程队，相当于Assembler
 * @author: Yzw
 * @date:
 * @param:
 * @return:
 **/
class ConstructionalForce implements Runnable {
    private HouseQueue foundationQueue, finishingQueue;
    private House foundation;
    private CyclicBarrier barrier = new CyclicBarrier(4);
    private WorkerPool workerPool;

    public ConstructionalForce(HouseQueue foundationQueue, HouseQueue finishingQueue, WorkerPool workerPool) {
        this.foundationQueue = foundationQueue;
        this.finishingQueue = finishingQueue;
        this.workerPool = workerPool;
    }

    public House foundation() {
        return foundation;
    }

    public CyclicBarrier barrier() {
        return barrier;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                foundation = foundationQueue.take();
                workerPool.hire(PouringWorker.class, this);
                workerPool.hire(SteelWorker.class, this);
                workerPool.hire(CementWorker.class, this);

                barrier.await();
                finishingQueue.put(foundation);
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting ConstructionalForce via interrupt");
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        System.out.println("ConstructionalForce off");
    }
}

class Reporter implements Runnable {
    private HouseQueue houseQueue;

    public Reporter(HouseQueue houseQueue) {
        this.houseQueue = houseQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(houseQueue.take());
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting Reporter via interrupt");
        }

        System.out.println("Reporter off");
    }
}

abstract class Worker implements Runnable {
    private WorkerPool pool;

    public Worker(WorkerPool robotPool) {
        this.pool = robotPool;
    }

    protected ConstructionalForce constructionalForce;

    public Worker assignAssembler(ConstructionalForce constructionalForce) {
        this.constructionalForce = constructionalForce;
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
                constructionalForce.barrier().await();
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
        constructionalForce = null;
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

/**
 * @description: 混凝土浇注的工人
 * @author: Yzw
 * @date:
 * @param:
 * @return:
 **/
class PouringWorker extends Worker {
    public PouringWorker(WorkerPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " pouring concrete");
        constructionalForce.foundation().pourConcrete();
    }
}

/**
 * @description: 钢结构铺设的工人
 * @author: Yzw
 * @date:
 * @param:
 * @return:
 **/
class SteelWorker extends Worker {
    public SteelWorker(WorkerPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " laying steel structure");
        constructionalForce.foundation().steelStructured();
    }
}

/**
 * @description: 构建水泥部件的工人
 * @author: Yzw
 * @date:
 * @param:
 * @return:
 **/
class CementWorker extends Worker {
    public CementWorker(WorkerPool robotPool) {
        super(robotPool);
    }

    @Override
    protected void performService() {
        System.out.println(this + " constructing cement parts");
        constructionalForce.foundation().constructCement();
    }
}

class WorkerPool {
    private Set<Worker> pool = new HashSet<>();

    public synchronized void add(Worker r) {
        pool.add(r);
        notifyAll();
    }

    public synchronized void hire(Class<? extends Worker> robotType, ConstructionalForce d)
        throws InterruptedException {
        for (Worker r : pool) {
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

    public synchronized void release(Worker r) {
        add(r);
    }
}
