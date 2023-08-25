package chapter3.atomicity;

import java.util.concurrent.atomic.AtomicInteger;

import chapter3.EvenChecker;
import chapter3.IntGenerator;

/**
 * @ClassNAME MutexEvenGenerator
 * @Description 用AtomicInteger重写MutexEvenGenerator
 * @Author yu
 * @Date 2023/8/25 15:04
 * @Version 1.0
 */
public class AtomicEvenGenerator extends IntGenerator {
    private AtomicInteger currentEvenValue = new AtomicInteger(0);

    public int next() {
        return currentEvenValue.addAndGet(2);
    }

    public static void main(String[] args) {
        EvenChecker.test(new AtomicEvenGenerator());
    }
}
