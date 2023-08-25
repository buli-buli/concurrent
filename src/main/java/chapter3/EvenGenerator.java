package chapter3;

/**
 * @ClassNAME EvenGenerator
 * @Description 偶数生成器
 * @Author yu
 * @Date 2023/8/24 14:35
 * @Version 1.0
 */
public class EvenGenerator extends IntGenerator {
    private int currentEvenValue = 0;

    @Override
    public int next() {
        ++currentEvenValue;// 这里是一个危险点
        // 一个任务有可能在另一个任务执行第一个递增之后，但是还没有执行第二个递增操作之前，调用next()方法。
        // 这将使这个值处于不恰当的状态
        ++currentEvenValue;
        return currentEvenValue;
    }

    public static void main(String[] args) {
        EvenChecker.test(new EvenGenerator());
    }
}
