package chapter9;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassNAME SimpleMicroBenchMark
 * @Description 比较天真的测试性能的方式——针对每种方式都执行一个简单的测试 本示例演示了所谓的”微基准测试“危险，这个术语通常指在隔离的、脱离上下 文环境的情况下对某 个特性进行性能测试。
 *              首先，我们只有在这些互斥存在竞争条件的情况下，才能看到真正的性能差异，因 此必须由多个任务尝试着访问互斥代码区。其次，当编译器看到synchronized关键字
 *              时，有可能会执行特殊的优化，甚至有可能会注意到这个程序是单线程的。编译器甚至 可能会识别出counter被递增的次数是固定数量的，因此会预先计算出其结果。
 * @Author yu
 * @Date 2023/9/1 14:36
 * @Version 1.0
 */
public class SimpleMicroBenchMark {
    static long test(Incrementable incr) {
        long start = System.nanoTime();
        for (long i = 0; i < 10000000L; i++) {
            incr.increment();
        }

        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        long synchTime = test(new SynchronizingTest());
        long lockTime = test(new LockingTest());
        System.out.printf("synchronized: %1$10d\n", synchTime);
        System.out.printf("Lock: %1$10d\n", lockTime);
        System.out.printf("Lock/synchronized = %1$.3f", (double)lockTime / (double)synchTime);
        System.out.println();
    }
}

abstract class Incrementable {
    protected long counter = 0;

    public abstract void increment();
}

class SynchronizingTest extends Incrementable {

    @Override
    public synchronized void increment() {
        ++counter;
    }
}

class LockingTest extends Incrementable {
    private Lock lock = new ReentrantLock();

    @Override
    public void increment() {
        lock.lock();
        try {
            ++counter;
        } finally {
            lock.unlock();
        }
    }
}
