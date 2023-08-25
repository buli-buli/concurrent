package chapter3.atomicity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME AtomicityTest
 * @Description 在java中，即使只是简单地对基础类型数据进行加减地操作，也不是原子性的。 换句话说，即使是这么简单的操作，也有产生并发问题的空间
 * @Author yu
 * @Date 2023/8/24 17:01
 * @Version 1.0
 */
public class AtomicityTest implements Runnable {
    private int i = 0;

    public int getValue() {
        return i;
    }

    private synchronized void evenIncrement() {
        i++;
        i++;
    }

    @Override
    public void run() {
        while (true) {
            evenIncrement();
        }
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        AtomicityTest at = new AtomicityTest();
        exec.execute(at);

        while (true) {
            int val = at.getValue();
            if (val % 2 != 0) {
                System.out.println(val);
                System.exit(0);
            }
        }
    }
}
