package chapter3.section;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassNAME ExplicitCriticalSection
 * @Description 使用显式的Lock对象构建临界区
 * @Author yu
 * @Date 2023/8/25 16:30
 * @Version 1.0
 */
public class ExplicitCriticalSection {
    public static void main(String[] args) {
        PairManager pman1 = new ExplicitManager1(), pman2 = new ExplicitManager2();

        CriticalSection.testApproaches(pman1, pman2);
    }
}

class ExplicitManager1 extends PairManager {
    private Lock lock = new ReentrantLock();

    @Override
    public synchronized void increment() {
        lock.lock();
        try {
            p.incrementX();
            p.incrementY();
            store(getPair());
        } finally {
            lock.unlock();
        }
    }
}

class ExplicitManager2 extends PairManager {
    private Lock lock = new ReentrantLock();

    @Override
    public void increment() {
        Pair temp;
        lock.lock();
        try {
            p.incrementX();
            p.incrementY();
            temp = getPair();
        } finally {
            lock.unlock();
        }
        store(temp);
    }
}