package chapter3.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import chapter3.EvenChecker;
import chapter3.IntGenerator;

/**
 * @ClassNAME MutexEventGenerator
 * @Description 使用显式的Lock对象实现互斥地访问共享资源
 * @Author yu
 * @Date 2023/8/24 15:47
 * @Version 1.0
 */
public class MutexEventGenerator extends IntGenerator {
    private int currentEvenValue = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public int next() {
        lock.lock();

        try {
            ++currentEvenValue;
            ++currentEvenValue;
            return currentEvenValue;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        EvenChecker.test(new MutexEventGenerator());
    }
}
