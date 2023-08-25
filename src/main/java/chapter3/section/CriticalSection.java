package chapter3.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassNAME CriticalSection
 * @Description 21.3.5 临界区代码示例。也被称为同步控制快 通过使用同步控制块 ，而不是对整个方法进行同步控制，可以使多个任务访问对象的时间性能得到显著提高
 * 
 *              从PairManipulator的checkCounter字段记录的检查次数可以看出 ，采用同步控制块而不是对整个方法进行同步控制使得其他线程能更多地访问共享资源（在安全的情况下尽可能的多）
 * @Author yu
 * @Date 2023/8/25 15:39
 * @Version 1.0
 */
public class CriticalSection {
    static void testApproaches(PairManager pman1, PairManager pman2) {
        ExecutorService exec = Executors.newCachedThreadPool();

        PairManipulator pm1 = new PairManipulator(pman1), pm2 = new PairManipulator(pman2);

        PairChecker pcheck1 = new PairChecker(pman1), pcheck2 = new PairChecker(pman2);

        exec.execute(pm1);
        exec.execute(pm2);
        exec.execute(pcheck1);
        exec.execute(pcheck2);

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }

        System.out.println("pm1: " + pm1 + "\npm2: " + pm2);
        System.exit(0);
    }

    public static void main(String[] args) {
        PairManager pman1 = new PairManager1(), pman2 = new PairManager2();
        testApproaches(pman1, pman2);
    }
}

class Pair {
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {
        this(0, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX() {
        x++;
    }

    public void incrementY() {
        y++;
    }

    @Override
    public String toString() {
        return "Pair{" + "x=" + x + ", y=" + y + '}';
    }

    public class PairValuesNotEqualException extends RuntimeException {
        public PairValuesNotEqualException() {
            super("Pair values not equal: " + Pair.this);
        }
    }

    public void checkState() {
        if (x != y) {
            throw new PairValuesNotEqualException();
        }
    }
}

// PairManger的一些功能在基类中实现，并且一个或多个抽象方法在派生类中定义。
// 这种结构在设计模式中成为模板方法
// 设计模式使你得以把变化封装在代码里；在此，发生变化的模板方法increment()
abstract class PairManager {
    AtomicInteger checkCounter = new AtomicInteger(0);
    protected Pair p = new Pair();
    private List<Pair> storage = Collections.synchronizedList(new ArrayList<>());

    public synchronized Pair getPair() {
        return new Pair(p.getX(), p.getY());
    }

    protected void store(Pair p) {
        storage.add(p);
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
        }
    }

    public abstract void increment();
}

class PairManager1 extends PairManager {

    @Override
    public synchronized void increment() {
        p.incrementX();
        p.incrementY();
        store(getPair());
    }
}

class PairManager2 extends PairManager {

    @Override
    public void increment() {
        Pair temp;

        synchronized (this) {
            p.incrementX();
            p.incrementY();
            temp = getPair();
        }

        store(temp);
    }
}

class PairManipulator implements Runnable {
    private PairManager pm;

    public PairManipulator(PairManager pm) {
        this.pm = pm;
    }

    @Override
    public void run() {
        while (true) {
            pm.increment();
        }
    }

    @Override
    public String toString() {
        return "pair : " + pm.getPair() + " checkCounter = " + pm.checkCounter.get();
    }
}

class PairChecker implements Runnable {
    private PairManager pm;

    public PairChecker(PairManager pm) {
        this.pm = pm;
    }

    @Override
    public void run() {
        while (true) {
            pm.checkCounter.incrementAndGet();
            pm.getPair().checkState();
        }
    }
}