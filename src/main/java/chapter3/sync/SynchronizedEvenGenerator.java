package chapter3.sync;

import chapter3.EvenChecker;
import chapter3.IntGenerator;

/**
 * @ClassNAME SynchronizedEvenGenerator
 * @Description 加入synchronized关键字同步控制 每个访问临界共享资源的方法都必须被同步，否则它们就不会正确的工作
 * @Author yu
 * @Date 2023/8/24 15:25
 * @Version 1.0
 */
public class SynchronizedEvenGenerator extends IntGenerator {
    private int currentEvenValue = 0;

    @Override
    public synchronized int next() {
        ++currentEvenValue;// 这里是一个危险点
        // 一个任务有可能在另一个任务执行第一个递增之后，但是还没有执行第二个递增操作之前，调用next()方法。
        // 这将使这个值处于不恰当的状态
        ++currentEvenValue;
        return currentEvenValue;
    }

    public static void main(String[] args) {
        EvenChecker.test(new SynchronizedEvenGenerator());
    }
}
